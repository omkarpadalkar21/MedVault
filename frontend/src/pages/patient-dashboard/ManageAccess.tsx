import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Search, QrCode } from "lucide-react";

export default function ManageAccess() {
  const [searchQuery, setSearchQuery] = useState("");
  const [accessType, setAccessType] = useState("");
  const [emergencyCardData, setEmergencyCardData] = useState({
    nameAge: false,
    allergies: false,
    currentMedications: false,
    emergencyContact: false,
    majorConditions: false,
    doctorContact: false,
    recentHospitalizations: false,
  });

  const grantedAccess = [
    {
      id: "1",
      name: "Dr. Mehta",
      organization: "AIIMs",
      accessType: "View Only",
      expiry: "3 days",
    },
  ];

  return (
    <div className="p-8 space-y-6">
      <div>
        <h1 className="text-3xl font-bold mb-2">Record Access & Emergency Card Manager</h1>
        <p className="text-muted-foreground">
          Manage who can view your records and what's shown in emergencies.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Grant Access Section */}
        <Card>
          <CardHeader>
            <CardTitle>Grant Access to Medical Records</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="search">Search by name or ID</Label>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                <Input
                  id="search"
                  placeholder="Search by name or ID"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            <div>
              <Label htmlFor="accessType">Access Type</Label>
              <Select value={accessType} onValueChange={setAccessType}>
                <SelectTrigger id="accessType">
                  <SelectValue placeholder="Select access type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="view">View Only</SelectItem>
                  <SelectItem value="edit">Edit Access</SelectItem>
                  <SelectItem value="full">Full Access</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <Button className="w-full bg-[#00BFA5] hover:bg-[#00A892]">
              Grant Access
            </Button>

            {/* Access List */}
            <div className="mt-6">
              <div className="grid grid-cols-3 gap-4 text-sm font-medium text-muted-foreground mb-3">
                <div>Name / Organization</div>
                <div>Access Type</div>
                <div>Expiry</div>
              </div>
              {grantedAccess.map((access) => (
                <div
                  key={access.id}
                  className="grid grid-cols-3 gap-4 py-3 border-t text-sm"
                >
                  <div>
                    <div className="font-medium">{access.name}</div>
                    <div className="text-muted-foreground">{access.organization}</div>
                  </div>
                  <div>{access.accessType}</div>
                  <div>{access.expiry}</div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Emergency Card Section */}
        <Card>
          <CardHeader>
            <CardTitle>Create Emergency Card</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Select the information that will be visible to emergency responders if your
              health card is scanned
            </p>

            <div className="space-y-3">
              <div className="flex items-center space-x-2">
                <Checkbox
                  id="nameAge"
                  checked={emergencyCardData.nameAge}
                  onCheckedChange={(checked) =>
                    setEmergencyCardData({ ...emergencyCardData, nameAge: checked as boolean })
                  }
                />
                <Label htmlFor="nameAge" className="cursor-pointer">
                  Name, Age, Blood Group
                </Label>
              </div>

              <div className="flex items-center space-x-2">
                <Checkbox
                  id="allergies"
                  checked={emergencyCardData.allergies}
                  onCheckedChange={(checked) =>
                    setEmergencyCardData({ ...emergencyCardData, allergies: checked as boolean })
                  }
                />
                <Label htmlFor="allergies" className="cursor-pointer">
                  Allergies
                </Label>
              </div>

              <div className="flex items-center space-x-2">
                <Checkbox
                  id="currentMedications"
                  checked={emergencyCardData.currentMedications}
                  onCheckedChange={(checked) =>
                    setEmergencyCardData({
                      ...emergencyCardData,
                      currentMedications: checked as boolean,
                    })
                  }
                />
                <Label htmlFor="currentMedications" className="cursor-pointer">
                  Current Medications
                </Label>
              </div>

              <div className="flex items-center space-x-2">
                <Checkbox
                  id="emergencyContact"
                  checked={emergencyCardData.emergencyContact}
                  onCheckedChange={(checked) =>
                    setEmergencyCardData({
                      ...emergencyCardData,
                      emergencyContact: checked as boolean,
                    })
                  }
                />
                <Label htmlFor="emergencyContact" className="cursor-pointer">
                  Emergency Contact
                </Label>
              </div>

              <div className="flex items-center space-x-2">
                <Checkbox
                  id="majorConditions"
                  checked={emergencyCardData.majorConditions}
                  onCheckedChange={(checked) =>
                    setEmergencyCardData({
                      ...emergencyCardData,
                      majorConditions: checked as boolean,
                    })
                  }
                />
                <Label htmlFor="majorConditions" className="cursor-pointer">
                  Major Conditions
                </Label>
              </div>

              <div className="flex items-center space-x-2">
                <Checkbox
                  id="doctorContact"
                  checked={emergencyCardData.doctorContact}
                  onCheckedChange={(checked) =>
                    setEmergencyCardData({
                      ...emergencyCardData,
                      doctorContact: checked as boolean,
                    })
                  }
                />
                <Label htmlFor="doctorContact" className="cursor-pointer">
                  Doctor Contact
                </Label>
              </div>

              <div className="flex items-center space-x-2">
                <Checkbox
                  id="recentHospitalizations"
                  checked={emergencyCardData.recentHospitalizations}
                  onCheckedChange={(checked) =>
                    setEmergencyCardData({
                      ...emergencyCardData,
                      recentHospitalizations: checked as boolean,
                    })
                  }
                />
                <Label htmlFor="recentHospitalizations" className="cursor-pointer">
                  Recent Hospitalizations
                </Label>
              </div>
            </div>

            <div className="flex gap-3 pt-4">
              <Button variant="outline" className="flex-1">
                Preview Emergency Card
              </Button>
              <Button className="flex-1 bg-[#00BFA5] hover:bg-[#00A892]">
                <QrCode className="w-4 h-4 mr-2" />
                Generate QR Card
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
