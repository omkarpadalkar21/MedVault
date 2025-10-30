import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import ManageAccess from "./ManageAccess";
import Documents from "./Documents";
import Imaging from "./Imaging";
import Communication from "./Communication";
import {
  Search,
  Bell,
  Settings,
  LogOut,
  ChevronDown,
  ChevronUp,
  Eye,
  Download,
  Heart,
  Wind,
  Activity,
  Zap,
  Home,
  FileText,
  FlaskConical,
  Image as ImageIcon,
  MessageSquare,
  ChevronLeft,
  ChevronRight,
  Shield,
} from "lucide-react";

interface DiagnosisItem {
  id: string;
  year: string;
  title: string;
  description: string;
  color: "yellow" | "teal";
  expanded?: boolean;
}

interface Document {
  id: string;
  title: string;
  category: string;
  doctor: string;
  time: string;
  icon: "activity" | "file" | "flask";
}

interface SystemData {
  name: string;
  icon: typeof Heart;
  metrics: string;
  status: string;
  statusColor: string;
  bgColor: string;
  borderColor: string;
  iconColor: string;
}

export default function PatientDashboard() {
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [activeDocCategory, setActiveDocCategory] = useState("All Documents");
  const [activeSystem, setActiveSystem] = useState<string>("Circulatory");
  const [activeView, setActiveView] = useState<string>("overview");
  const [unreadMessages, setUnreadMessages] = useState(3);
  const [totalDocuments, setTotalDocuments] = useState(24);
  const [totalImages, setTotalImages] = useState(12);
  const [diagnosisItems, setDiagnosisItems] = useState<DiagnosisItem[]>([
    {
      id: "1",
      year: "2025",
      title: "Hypertension Management",
      description: "Ongoing blood pressure monitoring and medication adjustment",
      color: "yellow",
      expanded: true,
    },
    {
      id: "2",
      year: "2024",
      title: "Type 2 Diabetes",
      description: "",
      color: "yellow",
      expanded: false,
    },
    {
      id: "3",
      year: "2023",
      title: "Elevated Cholesterol",
      description: "",
      color: "teal",
      expanded: false,
    },
    {
      id: "4",
      year: "2022",
      title: "Seasonal Allergies",
      description: "",
      color: "teal",
      expanded: false,
    },
    {
      id: "5",
      year: "2021",
      title: "Routine Checkup",
      description: "",
      color: "teal",
      expanded: false,
    },
  ]);

  const systemsData: Record<string, SystemData> = {
    Circulatory: {
      name: "Circulatory System",
      icon: Heart,
      metrics: "Heart rate: 72 bpm, BP: 120/80 mmHg",
      status: "Normal",
      statusColor: "bg-green-500 hover:bg-green-600",
      bgColor: "bg-pink-50",
      borderColor: "border-pink-200",
      iconColor: "text-pink-500",
    },
    Respiratory: {
      name: "Respiratory System",
      icon: Wind,
      metrics: "Respiratory rate: 16 breaths/min, SpO2: 98%",
      status: "Normal",
      statusColor: "bg-green-500 hover:bg-green-600",
      bgColor: "bg-blue-50",
      borderColor: "border-blue-200",
      iconColor: "text-blue-500",
    },
    Nervous: {
      name: "Nervous System",
      icon: Activity,
      metrics: "Reflexes: Normal, Cognitive function: Good",
      status: "Normal",
      statusColor: "bg-green-500 hover:bg-green-600",
      bgColor: "bg-purple-50",
      borderColor: "border-purple-200",
      iconColor: "text-purple-500",
    },
    Endocrine: {
      name: "Endocrine System",
      icon: Zap,
      metrics: "Blood glucose: 95 mg/dL, Thyroid: Normal",
      status: "Normal",
      statusColor: "bg-green-500 hover:bg-green-600",
      bgColor: "bg-yellow-50",
      borderColor: "border-yellow-200",
      iconColor: "text-yellow-600",
    },
  };

  const currentSystem = systemsData[activeSystem];

  const documents: Document[] = [
    {
      id: "1",
      title: "Visit Neurology",
      category: "Neurology",
      doctor: "Dr. Peterson",
      time: "16:31",
      icon: "activity",
    },
    {
      id: "2",
      title: "Abdomen",
      category: "Abdomen",
      doctor: "Dr. Anderson",
      time: "8:31",
      icon: "activity",
    },
    {
      id: "3",
      title: "ECG",
      category: "Cardiology",
      doctor: "Dr. Johnson",
      time: "17:31",
      icon: "activity",
    },
    {
      id: "4",
      title: "Hospitalization: Cardiology",
      category: "Cardiology",
      doctor: "Dr. Garcia",
      time: "2:03",
      icon: "activity",
    },
    {
      id: "5",
      title: "Prothrombin Time",
      category: "Lab",
      doctor: "Dr. Peterson",
      time: "04:32",
      icon: "flask",
    },
    {
      id: "6",
      title: "Office Visit: Cardiology",
      category: "Cardiology",
      doctor: "Dr. Smith",
      time: "8:32",
      icon: "activity",
    },
  ];

  const toggleDiagnosis = (id: string) => {
    setDiagnosisItems(
      diagnosisItems.map((item) =>
        item.id === id ? { ...item, expanded: !item.expanded } : item
      )
    );
  };

  return (
    <div className="flex min-h-screen bg-background">
      {/* Sidebar */}
      <aside
        className={`${
          isSidebarCollapsed ? "w-16" : "w-64"
        } text-white flex flex-col transition-all duration-300 relative`}
        style={{
          background: "linear-gradient(180deg, hsl(211 66% 40%) 0%, hsl(186 78% 48%) 100%)"
        }}
      >
        {/* Toggle Button */}
        <button
          onClick={() => setIsSidebarCollapsed(!isSidebarCollapsed)}
          className="absolute -right-3 top-6 w-6 h-6 rounded-full flex items-center justify-center hover:opacity-90 transition-opacity border-2 border-background z-10"
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

        {/* Logo */}
        <div className="p-6">
          <div className="flex items-center gap-2">
            {!isSidebarCollapsed && (
              <Shield className="w-8 h-8 text-red-500"  />
            )}
            <div className="text-xl font-semibold">
              {isSidebarCollapsed ? "M+" : "MedVault"}
            </div>
          </div>
        </div>

        {/* Patient Info */}
        {!isSidebarCollapsed && (
          <div className="px-6 mb-6">
            <div className="flex items-center gap-3 mb-4">
              <Avatar className="w-12 h-12">
                <AvatarFallback className="bg-primary text-primary-foreground">
                  PM
                </AvatarFallback>
              </Avatar>
              <div>
                <h3 className="font-semibold">Prescott MacCaffery</h3>
                <p className="text-xs text-gray-400">Patient ID: 14838</p>
              </div>
            </div>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-300">Age:</span>
                <span className="font-medium">42 years</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-300">Blood Group:</span>
                <span className="font-medium">O+</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-300">Gender:</span>
                <span className="font-medium">Male</span>
              </div>
            </div>
          </div>
        )}

        {/* Navigation */}
        <nav className="flex-1 px-3 space-y-1">
          <button
            onClick={() => setActiveView("overview")}
            className={`w-full flex items-center gap-3 px-3 py-3 rounded-lg transition-colors ${
              activeView === "overview" ? "bg-white/20 hover:bg-white/30" : "hover:bg-white/10"
            } ${isSidebarCollapsed ? "justify-center" : ""}`}
          >
            <Home className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Overview</span>}
          </button>
          <button
            onClick={() => setActiveView("set-emergency-data")}
            className={`w-full flex items-center gap-3 px-3 py-3 rounded-lg transition-colors ${
              activeView === "set-emergency-data" ? "bg-white/20 hover:bg-white/30" : "hover:bg-white/10"
            } ${isSidebarCollapsed ? "justify-center" : ""}`}
          >
            <Shield className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Set Emergency Data</span>}
          </button>
          <button
            onClick={() => setActiveView("documents")}
            className={`w-full flex items-center gap-3 px-3 py-3 rounded-lg transition-colors ${
              activeView === "documents" ? "bg-white/20 hover:bg-white/30" : "hover:bg-white/10"
            } ${isSidebarCollapsed ? "justify-center" : ""}`}
          >
            <FileText className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Documents</span>}
          </button>
          <button
            onClick={() => setActiveView("imaging")}
            className={`w-full flex items-center gap-3 px-3 py-3 rounded-lg transition-colors ${
              activeView === "imaging" ? "bg-white/20 hover:bg-white/30" : "hover:bg-white/10"
            } ${isSidebarCollapsed ? "justify-center" : ""}`}
          >
            <ImageIcon className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Imaging</span>}
          </button>
          <button
            onClick={() => setActiveView("communication")}
            className={`w-full flex items-center gap-3 px-3 py-3 rounded-lg transition-colors ${
              activeView === "communication" ? "bg-white/20 hover:bg-white/30" : "hover:bg-white/10"
            } ${isSidebarCollapsed ? "justify-center" : ""}`}
          >
            <MessageSquare className="w-5 h-5 flex-shrink-0" />
            {!isSidebarCollapsed && <span>Communication</span>}
          </button>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col">
        {/* Header */}
        <header className="bg-white border-b px-8 py-4 flex items-center justify-between">
          <h1 className="text-2xl font-semibold">Patient Dashboard</h1>
          <div className="flex items-center gap-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <input
                type="text"
                placeholder="Search records..."
                className="pl-10 pr-4 py-2 border rounded-lg w-64 focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
            <button className="relative p-2 hover:bg-muted rounded-lg">
              <Bell className="w-5 h-5" />
              <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
            </button>
            <button className="p-2 hover:bg-muted rounded-lg">
              <Settings className="w-5 h-5" />
            </button>
            <button className="p-2 hover:bg-muted rounded-lg">
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </header>

        {/* Content Grid */}
        <div className="flex-1 overflow-auto">
          {activeView === "set-emergency-data" ? (
            <ManageAccess />
          ) : activeView === "documents" ? (
            <Documents />
          ) : activeView === "imaging" ? (
            <Imaging />
          ) : activeView === "communication" ? (
            <Communication />
          ) : (
            <div className="p-8">
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Left Column - Diagnosis Timeline */}
            <div className="lg:col-span-1">
              <Card>
                <CardHeader>
                  <CardTitle>Diagnosis Timeline</CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  {diagnosisItems.map((item, index) => (
                    <div key={item.id} className="relative">
                      {/* Timeline line */}
                      {index < diagnosisItems.length - 1 && (
                        <div className="absolute left-5 top-12 w-0.5 h-full bg-gray-200"></div>
                      )}
                      <div
                        className={`p-4 rounded-lg border-2 cursor-pointer transition-all ${
                          item.color === "yellow"
                            ? "bg-yellow-50 border-yellow-200"
                            : "bg-teal-50 border-teal-200"
                        }`}
                        onClick={() => toggleDiagnosis(item.id)}
                      >
                        <div className="flex items-start gap-3">
                          <div
                            className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${
                              item.color === "yellow"
                                ? "bg-yellow-400"
                                : "bg-teal-400"
                            }`}
                          >
                            <span className="text-white font-semibold text-sm">
                              {item.year.slice(-2)}
                            </span>
                          </div>
                          <div className="flex-1">
                            <div className="flex items-start justify-between">
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">
                                  {item.year}
                                </p>
                                <h4 className="font-semibold text-sm">
                                  {item.title}
                                </h4>
                              </div>
                              {item.expanded ? (
                                <ChevronUp className="w-4 h-4 text-muted-foreground" />
                              ) : (
                                <ChevronDown className="w-4 h-4 text-muted-foreground" />
                              )}
                            </div>
                            {item.expanded && item.description && (
                              <p className="text-xs text-muted-foreground mt-2">
                                {item.description}
                              </p>
                            )}
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </CardContent>
              </Card>
            </div>

            {/* Middle Column - System Categories */}
            <div className="lg:col-span-1">
              <Card className="h-full">
                <CardContent className="p-6">
                  <div className="space-y-6">
                    {/* Dynamic System Card */}
                    <div className={`${currentSystem.bgColor} border-2 ${currentSystem.borderColor} rounded-lg p-6 text-center transition-all duration-300`}>
                      <currentSystem.icon className={`w-12 h-12 ${currentSystem.iconColor} mx-auto mb-3`} />
                      <h3 className="font-semibold text-lg mb-2">
                        {currentSystem.name}
                      </h3>
                      <p className="text-sm text-muted-foreground mb-3">
                        {currentSystem.metrics}
                      </p>
                      <Badge className={currentSystem.statusColor}>
                        {currentSystem.status}
                      </Badge>
                    </div>

                    {/* System Buttons Grid */}
                    <div className="grid grid-cols-2 gap-3">
                      <button 
                        onClick={() => setActiveSystem("Circulatory")}
                        className={`p-4 border-2 rounded-lg transition-colors ${
                          activeSystem === "Circulatory" 
                            ? "border-red-400 bg-red-50" 
                            : "border-gray-200 hover:bg-gray-50"
                        }`}
                      >
                        <Heart className={`w-8 h-8 mx-auto mb-2 ${
                          activeSystem === "Circulatory" ? "text-red-500" : "text-gray-500"
                        }`} />
                        <span className="text-sm font-medium">Circulatory</span>
                      </button>
                      <button 
                        onClick={() => setActiveSystem("Respiratory")}
                        className={`p-4 border-2 rounded-lg transition-colors ${
                          activeSystem === "Respiratory" 
                            ? "border-blue-400 bg-blue-50" 
                            : "border-gray-200 hover:bg-gray-50"
                        }`}
                      >
                        <Wind className={`w-8 h-8 mx-auto mb-2 ${
                          activeSystem === "Respiratory" ? "text-blue-500" : "text-gray-500"
                        }`} />
                        <span className="text-sm font-medium">Respiratory</span>
                      </button>
                      <button 
                        onClick={() => setActiveSystem("Nervous")}
                        className={`p-4 border-2 rounded-lg transition-colors ${
                          activeSystem === "Nervous" 
                            ? "border-purple-400 bg-purple-50" 
                            : "border-gray-200 hover:bg-gray-50"
                        }`}
                      >
                        <Activity className={`w-8 h-8 mx-auto mb-2 ${
                          activeSystem === "Nervous" ? "text-purple-500" : "text-gray-500"
                        }`} />
                        <span className="text-sm font-medium">Nervous</span>
                      </button>
                      <button 
                        onClick={() => setActiveSystem("Endocrine")}
                        className={`p-4 border-2 rounded-lg transition-colors ${
                          activeSystem === "Endocrine" 
                            ? "border-yellow-400 bg-yellow-50" 
                            : "border-gray-200 hover:bg-gray-50"
                        }`}
                      >
                        <Zap className={`w-8 h-8 mx-auto mb-2 ${
                          activeSystem === "Endocrine" ? "text-yellow-600" : "text-gray-500"
                        }`} />
                        <span className="text-sm font-medium">Endocrine</span>
                      </button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Right Column - Documents */}
            <div className="lg:col-span-1">
              <Card>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <CardTitle>Documents</CardTitle>
                    <span className="text-sm text-muted-foreground">
                      6 documents
                    </span>
                  </div>
                  <div className="flex gap-2 mt-4 flex-wrap">
                    {[
                      "All Documents",
                      "Neurology",
                      "Cardiology",
                      "Lab",
                      "General",
                    ].map((category) => (
                      <button
                        key={category}
                        onClick={() => setActiveDocCategory(category)}
                        className={`px-3 py-1 rounded-full text-xs font-medium transition-colors ${
                          activeDocCategory === category
                            ? "bg-[#00BFA5] text-white"
                            : "bg-muted hover:bg-muted/80"
                        }`}
                      >
                        {category}
                      </button>
                    ))}
                  </div>
                </CardHeader>
                <CardContent className="space-y-3">
                  {documents.map((doc) => (
                    <div
                      key={doc.id}
                      className="p-4 rounded-lg border hover:bg-muted/50 transition-colors"
                    >
                      <div className="flex items-start gap-3">
                        <div className="w-10 h-10 rounded-lg bg-teal-50 flex items-center justify-center flex-shrink-0">
                          {doc.icon === "flask" ? (
                            <FlaskConical className="w-5 h-5 text-teal-600" />
                          ) : (
                            <Activity className="w-5 h-5 text-teal-600" />
                          )}
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-start justify-between mb-1">
                            <h4 className="font-semibold text-sm">
                              {doc.title}
                            </h4>
                            <span className="text-xs text-muted-foreground">
                              {doc.time}
                            </span>
                          </div>
                          <p className="text-xs text-muted-foreground mb-2">
                            {doc.category}
                          </p>
                          <div className="flex items-center gap-1 text-xs text-muted-foreground">
                            <Avatar className="w-4 h-4">
                              <AvatarFallback className="bg-primary text-primary-foreground text-[8px]">
                                {doc.doctor
                                  .split(" ")
                                  .map((n) => n[0])
                                  .join("")}
                              </AvatarFallback>
                            </Avatar>
                            <span>{doc.doctor}</span>
                          </div>
                          <div className="flex gap-2 mt-3">
                            <Button
                              size="sm"
                              variant="outline"
                              className="flex-1 h-8 text-xs"
                            >
                              <Eye className="w-3 h-3 mr-1" />
                              View
                            </Button>
                            <Button
                              size="sm"
                              variant="outline"
                              className="flex-1 h-8 text-xs"
                            >
                              <Download className="w-3 h-3 mr-1" />
                              Download
                            </Button>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </CardContent>
              </Card>
            </div>
          </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
