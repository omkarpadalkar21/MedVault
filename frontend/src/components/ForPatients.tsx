import { Card, CardContent } from "@/components/ui/card";
import { FileCheck, UserCheck, AlertCircle, Eye } from "lucide-react";
import { Button } from "@/components/ui/button";
import doctorTablet from "@/assets/doctor-tablet.jpg";
import { AspectRatio } from "@/components/ui/aspect-ratio";

const benefits = [
  {
    icon: FileCheck,
    title: "One Unified Record",
    description: "Aggregate medical data from multiple hospitals, clinics, and labs in a single, secure platform",
  },
  {
    icon: UserCheck,
    title: "Grant Access Instantly",
    description: "Share your health records with doctors using simple OTP-based consent—no more carrying paper files",
  },
  {
    icon: AlertCircle,
    title: "Emergency Ready",
    description: "Pre-configure limited health data for emergency situations while maintaining your privacy",
  },
  {
    icon: Eye,
    title: "Track Every Access",
    description: "See exactly who viewed your records, when, and what they accessed with complete transparency",
  },
];

const ForPatients = () => {
  return (
    <section className="py-20 bg-gradient-subtle">
      <div className="container mx-auto px-4">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div className="order-2 md:order-1">
            <Card className="rounded-2xl shadow-elegant-lg overflow-hidden">
              <AspectRatio ratio={4 / 3}>
                <img
                  src={doctorTablet}
                  alt="Doctor using digital tablet for patient records"
                  className="w-full h-full object-cover"
                />
              </AspectRatio>
            </Card>
          </div>
          <div className="order-1 md:order-2 space-y-6">
            <div className="space-y-3">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                Your Health Data, Your Control
              </h2>
              <p className="text-xl text-muted-foreground">
                Store all your medical records in one secure place and decide who gets access
              </p>
            </div>
            <div className="space-y-4">
              {benefits.map((benefit, index) => (
                <Card key={index} className="hover:shadow-elegant transition-all duration-300 border-border bg-card">
                  <CardContent className="p-5">
                    <div className="flex gap-4">
                      <div className="flex-shrink-0">
                        <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
                          <benefit.icon className="w-6 h-6 text-primary" />
                        </div>
                      </div>
                      <div>
                        <h2 className="font-semibold text-card-foreground mb-1 text-[1.125rem]">{benefit.title}</h2 >
                        <p className="text-muted-foreground text-[1.125rem]">{benefit.description}</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
            <Button size="lg" className="mt-6">
              Sign Up as Patient →
            </Button>
          </div>
        </div>
      </div>
    </section>
  );
};

export default ForPatients;
