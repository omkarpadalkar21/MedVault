import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Search, Eye, Download, FileText, Activity, FlaskConical, Calendar } from "lucide-react";
import { useState } from "react";

export default function Documents() {
  const [searchQuery, setSearchQuery] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");

  const documents = [
    {
      id: "1",
      title: "Annual Physical Examination Report",
      category: "General",
      doctor: "Dr. Sarah Johnson",
      date: "Oct 15, 2025",
      time: "10:30 AM",
      type: "Report",
      icon: FileText,
      size: "2.4 MB",
    },
    {
      id: "2",
      title: "Cardiology Consultation Notes",
      category: "Cardiology",
      doctor: "Dr. Michael Chen",
      date: "Oct 10, 2025",
      time: "2:15 PM",
      type: "Consultation",
      icon: Activity,
      size: "1.8 MB",
    },
    {
      id: "3",
      title: "Blood Test Results - Complete Panel",
      category: "Lab",
      doctor: "Dr. Emily Peterson",
      date: "Oct 8, 2025",
      time: "9:00 AM",
      type: "Lab Report",
      icon: FlaskConical,
      size: "856 KB",
    },
    {
      id: "4",
      title: "Neurology Follow-up Visit",
      category: "Neurology",
      doctor: "Dr. James Anderson",
      date: "Oct 5, 2025",
      time: "4:30 PM",
      type: "Follow-up",
      icon: Activity,
      size: "1.2 MB",
    },
    {
      id: "5",
      title: "Prescription - Hypertension Medication",
      category: "Pharmacy",
      doctor: "Dr. Sarah Johnson",
      date: "Oct 3, 2025",
      time: "11:45 AM",
      type: "Prescription",
      icon: FileText,
      size: "324 KB",
    },
    {
      id: "6",
      title: "Lipid Profile Test Results",
      category: "Lab",
      doctor: "Dr. Emily Peterson",
      date: "Sep 28, 2025",
      time: "8:30 AM",
      type: "Lab Report",
      icon: FlaskConical,
      size: "645 KB",
    },
    {
      id: "7",
      title: "Diabetes Management Plan",
      category: "Endocrinology",
      doctor: "Dr. Robert Martinez",
      date: "Sep 25, 2025",
      time: "3:00 PM",
      type: "Treatment Plan",
      icon: FileText,
      size: "2.1 MB",
    },
    {
      id: "8",
      title: "Allergy Test Results",
      category: "Immunology",
      doctor: "Dr. Lisa Wong",
      date: "Sep 20, 2025",
      time: "10:00 AM",
      type: "Lab Report",
      icon: FlaskConical,
      size: "1.5 MB",
    },
  ];

  const categories = ["All", "General", "Cardiology", "Lab", "Neurology", "Pharmacy", "Endocrinology", "Immunology"];

  const filteredDocuments = documents.filter((doc) => {
    const matchesSearch = doc.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         doc.doctor.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = activeCategory === "All" || doc.category === activeCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="p-8 space-y-6">
      <div>
        <h1 className="text-3xl font-bold mb-2">Medical Documents</h1>
        <p className="text-muted-foreground">
          View and manage all your medical records and documents
        </p>
      </div>

      {/* Search and Filter */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                placeholder="Search documents..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <div className="flex gap-2 flex-wrap">
              {categories.map((category) => (
                <Button
                  key={category}
                  variant={activeCategory === category ? "default" : "outline"}
                  size="sm"
                  onClick={() => setActiveCategory(category)}
                  className={activeCategory === category ? "bg-[#00BFA5] hover:bg-[#00A892]" : ""}
                >
                  {category}
                </Button>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Documents Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {filteredDocuments.map((doc) => {
          const IconComponent = doc.icon;
          return (
            <Card key={doc.id} className="hover:shadow-md transition-shadow">
              <CardContent className="p-6">
                <div className="flex gap-4">
                  <div className="w-12 h-12 rounded-lg bg-teal-50 flex items-center justify-center flex-shrink-0">
                    <IconComponent className="w-6 h-6 text-teal-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between mb-2">
                      <div className="flex-1">
                        <h3 className="font-semibold text-base mb-1">{doc.title}</h3>
                        <div className="flex items-center gap-2 mb-2">
                          <Badge variant="secondary" className="text-xs">
                            {doc.category}
                          </Badge>
                          <Badge variant="outline" className="text-xs">
                            {doc.type}
                          </Badge>
                        </div>
                      </div>
                    </div>
                    
                    <div className="space-y-2 text-sm text-muted-foreground mb-3">
                      <div className="flex items-center gap-2">
                        <Avatar className="w-5 h-5">
                          <AvatarFallback className="bg-primary text-primary-foreground text-[10px]">
                            {doc.doctor.split(" ").map((n) => n[0]).join("")}
                          </AvatarFallback>
                        </Avatar>
                        <span>{doc.doctor}</span>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="flex items-center gap-1">
                          <Calendar className="w-4 h-4" />
                          <span>{doc.date}</span>
                        </div>
                        <span>•</span>
                        <span>{doc.time}</span>
                        <span>•</span>
                        <span>{doc.size}</span>
                      </div>
                    </div>

                    <div className="flex gap-2">
                      <Button size="sm" variant="outline" className="flex-1">
                        <Eye className="w-4 h-4 mr-1" />
                        View
                      </Button>
                      <Button size="sm" variant="outline" className="flex-1">
                        <Download className="w-4 h-4 mr-1" />
                        Download
                      </Button>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {filteredDocuments.length === 0 && (
        <Card>
          <CardContent className="p-12 text-center">
            <FileText className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
            <h3 className="text-lg font-semibold mb-2">No documents found</h3>
            <p className="text-muted-foreground">
              Try adjusting your search or filter criteria
            </p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
