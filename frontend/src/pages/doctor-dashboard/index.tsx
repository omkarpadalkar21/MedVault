import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useToast } from "@/components/ui/use-toast";
import { Search, Calendar, MessageSquare, Settings, ChevronDown, FileText, Activity, ChevronRight, ChevronLeft, Shield, Loader2, User, Phone, Mail, MapPin, Droplet, AlertCircle } from "lucide-react";
import axios from "axios";

const API_BASE_URL = "http://localhost:8080";

interface Patient {
  id: string;
  name: string;
  dob?: string;
  aadhaar: string;
  notes?: string;
}

interface PatientDetails {
  id: string;
  email: string;
  phoneNumber: string;
  isActive: boolean;
  mfaEnabled: boolean;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  bloodGroup: string;
  address: string;
  allergies?: string;
  chronicConditions?: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  createdAt: string;
  updatedAt: string;
}

interface Schedule {
  id: string;
  title: string;
  time: string;
  description: string;
  doctor: string;
  icon: "neurology" | "abdomen";
}

interface Notification {
  id: string;
  title: string;
  time: string;
  description: string;
  assignedTo: string;
  type: "document" | "activity";
}

export default function DoctorDashboard() {
  const [searchQuery, setSearchQuery] = useState("");
  const [activeTab, setActiveTab] = useState("ehr");
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [isSearching, setIsSearching] = useState(false);
  const [patientDetails, setPatientDetails] = useState<PatientDetails | null>(null);
  const { toast } = useToast();

  const recentPatients: Patient[] = [
    { id: "1", name: "John Doe", dob: "DOB: 10/11/1990", aadhaar: "Aadhaar: XXXX-XXXX-5634" },
    { id: "2", name: "John Doe", notes: "Follow-up with Mr. Sharma", aadhaar: "Aadhaar: XXX-XXX-5678" },
    { id: "3", name: "Jane Smith", notes: "Follow-up with Mr. Sharma", aadhaar: "XXXX-XXXX-5678" },
    { id: "4", name: "Jane Smith", aadhaar: "" },
  ];

  const todaysSchedule: Schedule[] = [
    {
      id: "1",
      title: "Visit Neurology",
      time: "12:30",
      description: "New to/pt up with Mr. Sharma",
      doctor: "Dr. Patel",
      icon: "neurology",
    },
    {
      id: "2",
      title: "Abdomen",
      time: "18:10",
      description: "New follow-up with Mr. Sharma",
      doctor: "Dr. Patel",
      icon: "abdomen",
    },
  ];

  const notifications: Notification[] = [
    {
      id: "1",
      title: "ALGE",
      time: "19:53",
      description: "New cur prelim up with Mr. Sharma",
      assignedTo: "Sarah Lee",
      type: "document",
    },
    {
      id: "2",
      title: "ECS Espiatory",
      time: "10:70",
      description: "New to spit up with Mr. Sarah Lee",
      assignedTo: "Dr. Patel",
      type: "document",
    },
    {
      id: "3",
      title: "Pulse Essentials",
      time: "19:26",
      description: "",
      assignedTo: "",
      type: "activity",
    },
  ];

  const handleSearchPatient = async () => {
    // Validate Aadhaar number
    if (!searchQuery.trim()) {
      toast({
        title: "Validation Error",
        description: "Please enter an Aadhaar number",
        variant: "destructive",
      });
      return;
    }

    // Remove any spaces or dashes from Aadhaar
    const cleanAadhaar = searchQuery.replace(/[\s-]/g, "");

    // Validate 12 digits
    if (!/^\d{12}$/.test(cleanAadhaar)) {
      toast({
        title: "Invalid Aadhaar",
        description: "Aadhaar number must be 12 digits",
        variant: "destructive",
      });
      return;
    }

    setIsSearching(true);
    setPatientDetails(null);

    try {
      const token = localStorage.getItem("accessToken");
      
      if (!token) {
        toast({
          title: "Authentication Required",
          description: "Please login to search for patients",
          variant: "destructive",
        });
        return;
      }

      const response = await axios.get<PatientDetails>(
        `${API_BASE_URL}/api/v1/patients/search/aadhaar/${cleanAadhaar}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setPatientDetails(response.data);
      toast({
        title: "Patient Found",
        description: `Found patient: ${response.data.firstName} ${response.data.lastName}`,
      });
    } catch (error: any) {
      console.error("Patient search error:", error);
      
      if (error.response?.status === 404) {
        toast({
          title: "Patient Not Found",
          description: "No patient found with the provided Aadhaar number",
          variant: "destructive",
        });
      } else if (error.response?.status === 403) {
        toast({
          title: "Access Denied",
          description: "You don't have permission to search for patients",
          variant: "destructive",
        });
      } else if (error.response?.status === 401) {
        toast({
          title: "Session Expired",
          description: "Please login again",
          variant: "destructive",
        });
      } else {
        toast({
          title: "Search Failed",
          description: error.response?.data?.message || "Something went wrong",
          variant: "destructive",
        });
      }
    } finally {
      setIsSearching(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearchPatient();
    }
  };

  return (
    <div className="flex min-h-screen bg-background">
      {/* Sidebar */}
      <aside
        className={`${isSidebarCollapsed ? 'w-20' : 'w-64'} text-white p-6 flex flex-col transition-all duration-300 relative`}
        style={{
          background: "linear-gradient(180deg, hsl(211 66% 40%) 0%, hsl(186 78% 48%) 100%)"
        }}
      >
        {/* Collapse Toggle Button */}
        <button
          onClick={() => setIsSidebarCollapsed(!isSidebarCollapsed)}
          className="absolute -right-3 top-8 w-6 h-6 rounded-full flex items-center justify-center hover:opacity-90 transition-opacity border-2 border-background z-10"
          style={{
            background: "linear-gradient(180deg, hsl(211 66% 40%) 0%, hsl(186 78% 48%) 100%)"
          }}
        >
          {isSidebarCollapsed ? (
            <ChevronRight className="w-4 h-4" />
          ) : (
            <ChevronLeft className="w-4 h-4" />
          )}
        </button>

        <div className={`flex items-center gap-2 mb-8 ${isSidebarCollapsed ? 'justify-center' : ''}`}>
          {!isSidebarCollapsed && (
            <Shield className="w-8 h-8 text-red-500" />
          )}
          <div className={`text-xl font-semibold ${isSidebarCollapsed ? '' : ''}`}>
            {isSidebarCollapsed ? "M+" : "MedVault"}
          </div>
        </div>

        {/* Doctor Profile */}
        <div className={`flex flex-col items-center mb-8 ${isSidebarCollapsed ? 'space-y-2' : ''}`}>
          <Avatar className={`${isSidebarCollapsed ? 'w-12 h-12' : 'w-24 h-24'} mb-4 transition-all duration-300`}>
            <AvatarImage src="/placeholder-doctor.jpg" alt="Dr. Emily Tran" />
            <AvatarFallback className="bg-primary text-primary-foreground text-2xl">ET</AvatarFallback>
          </Avatar>
          {!isSidebarCollapsed && (
            <>
              <h3 className="font-semibold text-lg">Dr. Emily Tran</h3>
              <p className="text-sm text-gray-300">Cardiologist</p>
            </>
          )}
        </div>

        {/* Navigation */}
        <nav className="flex-1 space-y-2">
          <button className={`w-full text-left px-4 py-3 rounded-lg bg-white/20 hover:bg-white/30 transition-colors flex items-center gap-3 ${isSidebarCollapsed ? 'justify-center' : ''}`}>
            <Calendar className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Dashboard / Patient Lookup</span>}
          </button>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 p-8">
        {/* Header Tabs */}
        <div className="flex gap-8 mb-8 border-b">
          <button
            onClick={() => setActiveTab("ehr")}
            className={`pb-2 px-1 font-medium transition-colors ${
              activeTab === "ehr"
                ? "text-foreground border-b-2 border-primary"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            EHR
          </button>
        </div>

        <div className="max-w-[40%]">
          <div className="space-y-6">
            {/* Patient Lookup */}
            <Card>
              <CardHeader>
                <CardTitle className="text-2xl">Patient Lookup</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex gap-2">
                    <div className="relative flex-1">
                      <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
                      <Input
                        placeholder="Enter Patient Aadhaar Number (12 digits)"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyPress={handleKeyPress}
                        className="pl-10"
                        disabled={isSearching}
                        maxLength={14}
                      />
                    </div>
                    <Button 
                      onClick={handleSearchPatient}
                      disabled={isSearching}
                      className="bg-[#00BFA5] hover:bg-[#00A892] text-white px-6"
                    >
                      {isSearching ? (
                        <>
                          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                          Searching...
                        </>
                      ) : (
                        "Search Patient"
                      )}
                    </Button>
                  </div>

                  {/* Patient Details Card */}
                  {patientDetails && (
                    <Card className="border-2 border-[#00BFA5]">
                      <CardHeader className="pb-3">
                        <CardTitle className="text-lg flex items-center gap-2">
                          <User className="w-5 h-5 text-[#00BFA5]" />
                          Patient Details
                        </CardTitle>
                      </CardHeader>
                      <CardContent className="space-y-4">
                        {/* Basic Info */}
                        <div className="grid grid-cols-2 gap-4">
                          <div>
                            <p className="text-sm text-muted-foreground">Full Name</p>
                            <p className="font-medium">{patientDetails.firstName} {patientDetails.lastName}</p>
                          </div>
                          <div>
                            <p className="text-sm text-muted-foreground">Date of Birth</p>
                            <p className="font-medium">{new Date(patientDetails.dateOfBirth).toLocaleDateString()}</p>
                          </div>
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                          <div>
                            <p className="text-sm text-muted-foreground">Gender</p>
                            <p className="font-medium">{patientDetails.gender}</p>
                          </div>
                          <div className="flex items-center gap-2">
                            <Droplet className="w-4 h-4 text-red-500" />
                            <div>
                              <p className="text-sm text-muted-foreground">Blood Group</p>
                              <p className="font-medium">{patientDetails.bloodGroup.replace('_', ' ')}</p>
                            </div>
                          </div>
                        </div>

                        {/* Contact Info */}
                        <div className="border-t pt-4 space-y-3">
                          <div className="flex items-start gap-2">
                            <Mail className="w-4 h-4 text-muted-foreground mt-0.5" />
                            <div>
                              <p className="text-sm text-muted-foreground">Email</p>
                              <p className="font-medium text-sm">{patientDetails.email}</p>
                            </div>
                          </div>
                          <div className="flex items-start gap-2">
                            <Phone className="w-4 h-4 text-muted-foreground mt-0.5" />
                            <div>
                              <p className="text-sm text-muted-foreground">Phone</p>
                              <p className="font-medium">{patientDetails.phoneNumber}</p>
                            </div>
                          </div>
                          <div className="flex items-start gap-2">
                            <MapPin className="w-4 h-4 text-muted-foreground mt-0.5" />
                            <div>
                              <p className="text-sm text-muted-foreground">Address</p>
                              <p className="font-medium text-sm">{patientDetails.address}</p>
                            </div>
                          </div>
                        </div>

                        {/* Medical Info */}
                        {(patientDetails.allergies || patientDetails.chronicConditions) && (
                          <div className="border-t pt-4 space-y-3">
                            {patientDetails.allergies && (
                              <div className="flex items-start gap-2">
                                <AlertCircle className="w-4 h-4 text-orange-500 mt-0.5" />
                                <div>
                                  <p className="text-sm text-muted-foreground">Allergies</p>
                                  <p className="font-medium text-sm">{patientDetails.allergies}</p>
                                </div>
                              </div>
                            )}
                            {patientDetails.chronicConditions && (
                              <div className="flex items-start gap-2">
                                <Activity className="w-4 h-4 text-blue-500 mt-0.5" />
                                <div>
                                  <p className="text-sm text-muted-foreground">Chronic Conditions</p>
                                  <p className="font-medium text-sm">{patientDetails.chronicConditions}</p>
                                </div>
                              </div>
                            )}
                          </div>
                        )}

                        {/* Emergency Contact */}
                        <div className="border-t pt-4 space-y-2">
                          <p className="text-sm font-medium text-muted-foreground">Emergency Contact</p>
                          <div className="flex items-center justify-between">
                            <p className="font-medium">{patientDetails.emergencyContactName}</p>
                            <p className="text-sm text-muted-foreground">{patientDetails.emergencyContactPhone}</p>
                          </div>
                        </div>

                        {/* Action Buttons */}
                        <div className="flex gap-2 pt-2">
                          <Button className="flex-1 bg-[#00BFA5] hover:bg-[#00A892]">
                            View Full EHR
                          </Button>
                          <Button variant="outline" className="flex-1">
                            Schedule Appointment
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  )}
                </div>
              </CardContent>
            </Card>

            {/* Recent Patients */}
            <Card>
              <CardHeader>
                <CardTitle className="text-xl">Recent Patients</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {recentPatients.map((patient) => (
                  <div
                    key={patient.id}
                    className="flex items-center gap-4 p-3 rounded-lg hover:bg-muted/50 transition-colors cursor-pointer"
                  >
                    <Avatar className="w-12 h-12">
                      <AvatarFallback className="bg-muted">
                        {patient.name
                          .split(" ")
                          .map((n) => n[0])
                          .join("")}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1">
                      <div className="font-medium">{patient.name}</div>
                      {patient.notes && (
                        <div className="text-sm text-muted-foreground">{patient.notes}</div>
                      )}
                      {patient.dob && (
                        <div className="text-sm text-muted-foreground">{patient.dob}</div>
                      )}
                      {patient.aadhaar && (
                        <div className="text-sm text-muted-foreground">{patient.aadhaar}</div>
                      )}
                    </div>
                    <ChevronDown className="w-5 h-5 text-muted-foreground" />
                  </div>
                ))}
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  );
}
