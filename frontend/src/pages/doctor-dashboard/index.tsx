import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Search, Calendar, MessageSquare, Settings, ChevronDown, FileText, Activity, ChevronRight, ChevronLeft } from "lucide-react";

interface Patient {
  id: string;
  name: string;
  dob?: string;
  aadhaar: string;
  notes?: string;
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

  return (
    <div className="flex min-h-screen bg-background">
      {/* Sidebar */}
      <aside className={`${isSidebarCollapsed ? 'w-20' : 'w-64'} bg-[#2C5F6F] text-white p-6 flex flex-col transition-all duration-300 relative`}>
        {/* Collapse Toggle Button */}
        <button
          onClick={() => setIsSidebarCollapsed(!isSidebarCollapsed)}
          className="absolute -right-3 top-8 w-6 h-6 bg-[#2C5F6F] rounded-full flex items-center justify-center hover:bg-[#1E4A5A] transition-colors border-2 border-background z-10"
        >
          {isSidebarCollapsed ? (
            <ChevronRight className="w-4 h-4" />
          ) : (
            <ChevronLeft className="w-4 h-4" />
          )}
        </button>

        <div className={`flex items-center gap-2 mb-8 ${isSidebarCollapsed ? 'justify-center' : ''}`}>
          <div className={`text-xl font-semibold ${isSidebarCollapsed ? 'hidden' : ''}`}>MedVault+</div>
          {isSidebarCollapsed && <div className="text-xl font-semibold">M+</div>}
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
          <button className={`w-full text-left px-4 py-3 rounded-lg bg-[#1E4A5A] hover:bg-[#1A3F4D] transition-colors flex items-center gap-3 ${isSidebarCollapsed ? 'justify-center' : ''}`}>
            <Calendar className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Dashboard / Patient Lookup</span>}
          </button>
          <button className={`w-full text-left px-4 py-3 rounded-lg hover:bg-[#1E4A5A] transition-colors flex items-center gap-3 ${isSidebarCollapsed ? 'justify-center' : ''}`}>
            <Calendar className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>My Schedule</span>}
          </button>
          <button className={`w-full text-left px-4 py-3 rounded-lg hover:bg-[#1E4A5A] transition-colors flex items-center gap-3 ${isSidebarCollapsed ? 'justify-center' : ''}`}>
            <MessageSquare className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Messages</span>}
          </button>
          <button className={`w-full text-left px-4 py-3 rounded-lg hover:bg-[#1E4A5A] transition-colors flex items-center gap-3 ${isSidebarCollapsed ? 'justify-center' : ''}`}>
            <Settings className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Settings</span>}
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

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left Column - Patient Lookup and Recent Patients */}
          <div className="lg:col-span-2 space-y-6">
            {/* Patient Lookup */}
            <Card>
              <CardHeader>
                <CardTitle className="text-2xl">Patient Lookup</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex gap-2">
                  <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-5 h-5" />
                    <Input
                      placeholder="Enter Patient Aadhaar or PAN Card Number"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                  <Button className="bg-[#00BFA5] hover:bg-[#00A892] text-white px-6">
                    Search Patient
                  </Button>
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

          {/* Right Column - Schedule and Notifications */}
          <div className="space-y-6">
            {/* Today's Schedule */}
            <Card>
              <CardHeader>
                <CardTitle className="text-xl">Today's Schedule</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {todaysSchedule.map((item) => (
                  <div
                    key={item.id}
                    className="p-4 rounded-lg bg-[#E0F2F1] border border-[#00BFA5]/20"
                  >
                    <div className="flex items-start gap-3">
                      <div className="w-10 h-10 rounded-full bg-[#00BFA5]/20 flex items-center justify-center flex-shrink-0">
                        {item.icon === "neurology" ? (
                          <Activity className="w-5 h-5 text-[#00BFA5]" />
                        ) : (
                          <Activity className="w-5 h-5 text-[#00BFA5]" />
                        )}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-start justify-between mb-1">
                          <h4 className="font-semibold">{item.title}</h4>
                          <span className="text-sm text-muted-foreground">{item.time}</span>
                        </div>
                        <p className="text-sm text-muted-foreground mb-2">{item.description}</p>
                        <div className="flex items-center gap-1 text-sm">
                          <Avatar className="w-4 h-4">
                            <AvatarFallback className="bg-primary text-primary-foreground text-xs">
                              P
                            </AvatarFallback>
                          </Avatar>
                          <span className="text-muted-foreground">{item.doctor}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>

            {/* Notifications */}
            <Card>
              <CardHeader>
                <CardTitle className="text-xl">Notifications</CardTitle>
                <p className="text-sm text-muted-foreground">Secledulss</p>
              </CardHeader>
              <CardContent className="space-y-3">
                {notifications.map((notification) => (
                  <div
                    key={notification.id}
                    className="p-4 rounded-lg border hover:bg-muted/50 transition-colors"
                  >
                    <div className="flex items-start gap-3">
                      <div className="w-10 h-10 rounded-lg bg-[#E0F2F1] flex items-center justify-center flex-shrink-0">
                        {notification.type === "document" ? (
                          <FileText className="w-5 h-5 text-[#00BFA5]" />
                        ) : (
                          <Activity className="w-5 h-5 text-[#00BFA5]" />
                        )}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-start justify-between mb-1">
                          <h4 className="font-semibold">{notification.title}</h4>
                          <span className="text-sm text-muted-foreground">{notification.time}</span>
                        </div>
                        {notification.description && (
                          <p className="text-sm text-muted-foreground mb-2">
                            {notification.description}
                          </p>
                        )}
                        {notification.assignedTo && (
                          <div className="flex items-center gap-1 text-sm">
                            <Avatar className="w-4 h-4">
                              <AvatarFallback className="bg-primary text-primary-foreground text-xs">
                                {notification.assignedTo[0]}
                              </AvatarFallback>
                            </Avatar>
                            <span className="text-muted-foreground">{notification.assignedTo}</span>
                          </div>
                        )}
                      </div>
                    </div>
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
