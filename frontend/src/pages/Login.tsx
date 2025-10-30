import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useNavigate } from "react-router-dom";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { useToast } from "@/components/ui/use-toast";
import axios from "axios";
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSlot,
} from "@/components/ui/input-otp";

// API Base URL - Update this to match your backend
const API_BASE_URL = "http://localhost:8080";

// Login schema
const loginSchema = z.object({
  email: z.string().email("Enter a valid email address"),
  password: z.string().min(12, "Password must be at least 12 characters"),
});

type LoginFormValues = z.infer<typeof loginSchema>;

// MFA verification schema
const mfaSchema = z.object({
  mfaCode: z.string().length(6, "MFA code must be 6 digits"),
});

type MFAFormValues = z.infer<typeof mfaSchema>;

const LoginPage = () => {
  const [mfaRequired, setMfaRequired] = useState(false);
  const [tempToken, setTempToken] = useState("");
  const [userEmail, setUserEmail] = useState("");
  const { toast } = useToast();
  const navigate = useNavigate();

  const loginForm = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const mfaForm = useForm<MFAFormValues>({
    resolver: zodResolver(mfaSchema),
    defaultValues: {
      mfaCode: "",
    },
  });

  function generateDeviceFingerprint(): string {
    try {
      const ua = navigator.userAgent;
      const lang = navigator.language;
      const plat = (navigator as any).platform || "unknown";
      return btoa([ua, lang, plat].join("|")).slice(0, 64);
    } catch {
      return "unknown-device";
    }
  }

  function getOrCreateDeviceId(): string {
    let deviceId = localStorage.getItem("deviceId");
    if (!deviceId) {
      deviceId = crypto.randomUUID();
      localStorage.setItem("deviceId", deviceId);
    }
    return deviceId;
  }

  async function handleLogin(values: LoginFormValues) {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/v1/auth/login`, {
        email: values.email,
        password: values.password,
        deviceId: getOrCreateDeviceId(),
        deviceFingerprint: generateDeviceFingerprint(),
      });

      const data = response.data;

      if (data.requiresMfa) {
        // MFA is required - show MFA form
        setMfaRequired(true);
        setTempToken(data.accessToken);
        setUserEmail(values.email);
        toast({
          title: "MFA Required",
          description: "Please enter the 6-digit code sent to your registered method.",
        });
      } else {
        // No MFA - login successful
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);
        localStorage.setItem("userRole", data.role);
        localStorage.setItem("userId", data.userId);

        toast({
          title: "Signed in successfully",
          description: `Welcome back, ${values.email}`,
        });

        // Navigate based on role
        if (data.role === "PATIENT") {
          navigate("/patient/dashboard");
        } else if (data.role === "DOCTOR") {
          navigate("/doctor/dashboard");
        } else if (data.role === "ADMIN") {
          navigate("/admin/dashboard");
        } else {
          navigate("/");
        }
      }
    } catch (error: any) {
      toast({
        title: "Login failed",
        description:
          error.response?.data?.message || "Invalid email or password",
        variant: "destructive",
      });
    }
  }

  async function handleMFAVerification(values: MFAFormValues) {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/api/v1/auth/verify-mfa`,
        {
          email: userEmail,
          mfaCode: values.mfaCode,
          tempToken: tempToken,
        }
      );

      const data = response.data;

      // Store tokens
      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);
      localStorage.setItem("userRole", data.role);
      localStorage.setItem("userId", data.userId);

      toast({
        title: "MFA Verified",
        description: "Authentication successful",
      });

      // Navigate based on role
      if (data.role === "PATIENT") {
        navigate("/patient/dashboard");
      } else if (data.role === "DOCTOR") {
        navigate("/doctor/dashboard");
      } else if (data.role === "ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/");
      }
    } catch (error: any) {
      toast({
        title: "MFA Verification failed",
        description: error.response?.data?.message || "Invalid MFA code",
        variant: "destructive",
      });
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <Card className="w-full max-w-md shadow-md">
        <CardHeader>
          <CardTitle className="text-2xl">
            {mfaRequired ? "Enter MFA Code" : "Sign in to MedVault"}
          </CardTitle>
        </CardHeader>
        <CardContent>
          {!mfaRequired ? (
            <Form {...loginForm}>
              <form
                onSubmit={loginForm.handleSubmit(handleLogin)}
                className="space-y-4"
              >
                <FormField
                  control={loginForm.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <Label htmlFor="email">Email</Label>
                      <FormControl>
                        <Input
                          id="email"
                          type="email"
                          placeholder="you@example.com"
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={loginForm.control}
                  name="password"
                  render={({ field }) => (
                    <FormItem>
                      <Label htmlFor="password">Password</Label>
                      <FormControl>
                        <Input
                          id="password"
                          type="password"
                          placeholder="••••••••••••"
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <Button type="submit" className="w-full">
                  Sign in
                </Button>
              </form>
            </Form>
          ) : (
            <Form {...mfaForm}>
              <form
                onSubmit={mfaForm.handleSubmit(handleMFAVerification)}
                className="space-y-4"
              >
                <FormField
                  control={mfaForm.control}
                  name="mfaCode"
                  render={({ field }) => (
                    <FormItem>
                      <Label htmlFor="mfaCode">6-Digit Code</Label>
                      <FormControl>
                        <InputOTP maxLength={6} {...field}>
                          <InputOTPGroup>
                            <InputOTPSlot index={0} />
                            <InputOTPSlot index={1} />
                            <InputOTPSlot index={2} />
                            <InputOTPSlot index={3} />
                            <InputOTPSlot index={4} />
                            <InputOTPSlot index={5} />
                          </InputOTPGroup>
                        </InputOTP>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <Button type="submit" className="w-full">
                  Verify MFA
                </Button>
              </form>
            </Form>
          )}
        </CardContent>
        <CardFooter className="flex justify-center">
          <p className="text-sm text-muted-foreground">
            Don't have an account?{" "}
            <Link to="/signup" className="text-primary hover:underline">
              Sign up
            </Link>
          </p>
        </CardFooter>
      </Card>
    </div>
  );
};

export default LoginPage;