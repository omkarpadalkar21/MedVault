import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Search, Eye, Download, Image as ImageIcon, Calendar, FileImage } from "lucide-react";
import { useState } from "react";

export default function Imaging() {
  const [searchQuery, setSearchQuery] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");

  const imagingRecords = [
    {
      id: "1",
      title: "Chest X-Ray - Routine Checkup",
      category: "X-Ray",
      doctor: "Dr. Sarah Johnson",
      date: "Oct 18, 2025",
      time: "11:00 AM",
      bodyPart: "Chest",
      findings: "Normal, no abnormalities detected",
      status: "Completed",
      images: 2,
    },
    {
      id: "2",
      title: "Brain MRI Scan",
      category: "MRI",
      doctor: "Dr. James Anderson",
      date: "Oct 12, 2025",
      time: "9:30 AM",
      bodyPart: "Brain",
      findings: "No significant abnormalities",
      status: "Completed",
      images: 24,
    },
    {
      id: "3",
      title: "Abdominal CT Scan",
      category: "CT Scan",
      doctor: "Dr. Michael Chen",
      date: "Oct 5, 2025",
      time: "2:00 PM",
      bodyPart: "Abdomen",
      findings: "Minor inflammation noted",
      status: "Completed",
      images: 48,
    },
    {
      id: "4",
      title: "Cardiac Ultrasound (Echocardiogram)",
      category: "Ultrasound",
      doctor: "Dr. Michael Chen",
      date: "Sep 28, 2025",
      time: "10:15 AM",
      bodyPart: "Heart",
      findings: "Normal cardiac function",
      status: "Completed",
      images: 12,
    },
    {
      id: "5",
      title: "Knee X-Ray - Left",
      category: "X-Ray",
      doctor: "Dr. Robert Martinez",
      date: "Sep 22, 2025",
      time: "3:45 PM",
      bodyPart: "Knee",
      findings: "Mild osteoarthritis",
      status: "Completed",
      images: 3,
    },
    {
      id: "6",
      title: "Spine MRI - Lumbar Region",
      category: "MRI",
      doctor: "Dr. James Anderson",
      date: "Sep 15, 2025",
      time: "1:30 PM",
      bodyPart: "Spine",
      findings: "Disc degeneration at L4-L5",
      status: "Completed",
      images: 32,
    },
    {
      id: "7",
      title: "Dental Panoramic X-Ray",
      category: "X-Ray",
      doctor: "Dr. Lisa Wong",
      date: "Sep 8, 2025",
      time: "4:00 PM",
      bodyPart: "Dental",
      findings: "All teeth healthy",
      status: "Completed",
      images: 1,
    },
    {
      id: "8",
      title: "Thyroid Ultrasound",
      category: "Ultrasound",
      doctor: "Dr. Emily Peterson",
      date: "Aug 30, 2025",
      time: "11:30 AM",
      bodyPart: "Thyroid",
      findings: "Normal thyroid size and structure",
      status: "Completed",
      images: 8,
    },
  ];

  const categories = ["All", "X-Ray", "MRI", "CT Scan", "Ultrasound"];

  const filteredRecords = imagingRecords.filter((record) => {
    const matchesSearch = record.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         record.doctor.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         record.bodyPart.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = activeCategory === "All" || record.category === activeCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="p-8 space-y-6">
      <div>
        <h1 className="text-3xl font-bold mb-2">Medical Imaging</h1>
        <p className="text-muted-foreground">
          View all your medical imaging records including X-rays, MRIs, CT scans, and ultrasounds
        </p>
      </div>

      {/* Search and Filter */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                placeholder="Search imaging records..."
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

      {/* Imaging Records Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {filteredRecords.map((record) => (
          <Card key={record.id} className="hover:shadow-md transition-shadow">
            <CardContent className="p-6">
              <div className="flex gap-4">
                <div className="w-12 h-12 rounded-lg bg-blue-50 flex items-center justify-center flex-shrink-0">
                  <ImageIcon className="w-6 h-6 text-blue-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between mb-2">
                    <div className="flex-1">
                      <h3 className="font-semibold text-base mb-1">{record.title}</h3>
                      <div className="flex items-center gap-2 mb-2">
                        <Badge variant="secondary" className="text-xs">
                          {record.category}
                        </Badge>
                        <Badge variant="outline" className="text-xs">
                          {record.bodyPart}
                        </Badge>
                        <Badge className="text-xs bg-green-500 hover:bg-green-600">
                          {record.status}
                        </Badge>
                      </div>
                    </div>
                  </div>
                  
                  <div className="space-y-2 text-sm text-muted-foreground mb-3">
                    <div className="flex items-center gap-2">
                      <Avatar className="w-5 h-5">
                        <AvatarFallback className="bg-primary text-primary-foreground text-[10px]">
                          {record.doctor.split(" ").map((n) => n[0]).join("")}
                        </AvatarFallback>
                      </Avatar>
                      <span>{record.doctor}</span>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="flex items-center gap-1">
                        <Calendar className="w-4 h-4" />
                        <span>{record.date}</span>
                      </div>
                      <span>•</span>
                      <span>{record.time}</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <FileImage className="w-4 h-4" />
                      <span>{record.images} image{record.images > 1 ? 's' : ''}</span>
                    </div>
                    <div className="mt-2 p-2 bg-muted rounded text-xs">
                      <strong>Findings:</strong> {record.findings}
                    </div>
                  </div>

                  <div className="flex gap-2">
                    <Button size="sm" variant="outline" className="flex-1">
                      <Eye className="w-4 h-4 mr-1" />
                      View Images
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
        ))}
      </div>

      {filteredRecords.length === 0 && (
        <Card>
          <CardContent className="p-12 text-center">
            <ImageIcon className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
            <h3 className="text-lg font-semibold mb-2">No imaging records found</h3>
            <p className="text-muted-foreground">
              Try adjusting your search or filter criteria
            </p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
