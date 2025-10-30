import { Card } from "@/components/ui/card";
import { KeyRound, AlertTriangle, Globe, FileText } from "lucide-react";
import { Button } from "@/components/ui/button";
import patientCare from "@/assets/patient-care.jpg";

const features = [
  {
    icon: KeyRound,
    title: "OTP-Based Access",
    description: "Request patient data access and verify with secure one-time passwords",
  },
  {
    icon: AlertTriangle,
    title: "Emergency Protocol",
    description: "Access critical patient information during emergencies using verified credentials",
  },
  {
    icon: Globe,
    title: "HL7 FHIR Compliant",
    description: "Seamlessly exchange data with other healthcare systems using international standards",
  },
  {
    icon: FileText,
    title: "Complete Audit Trail",
    description: "Every access is logged and traceable for regulatory compliance",
  },
];

const ForProviders = () => {
  return (
    <section className="py-20 bg-background">
      <div className="container mx-auto px-4">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div className="space-y-6">
            <div className="space-y-3">
              <h2 className="text-3xl md:text-4xl font-bold text-foreground">
                Streamline Patient Care with Instant Access
              </h2>
              <p className="text-lg text-muted-foreground">
                Access patient records securely with consent-based verification
              </p>
            </div>
            <div className="space-y-4">
              {features.map((feature, index) => (
                <Card key={index} className="p-5 hover:shadow-elegant transition-all duration-300 border-border bg-card">
                  <div className="flex gap-4">
                    <div className="flex-shrink-0">
                      <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                        <feature.icon className="w-6 h-6 text-accent" />
                      </div>
                    </div>
                    <div>
                      <h3 className="font-semibold text-card-foreground mb-1">{feature.title}</h3>
                      <p className="text-sm text-muted-foreground">{feature.description}</p>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
            <Button size="lg" variant="accent" className="mt-6">
              Register as Healthcare Provider →
            </Button>
          </div>
          <div>
            <img
              src={patientCare}
              alt="Healthcare provider and patients collaborating"
              className="rounded-2xl shadow-elegant-lg w-full h-auto"
            />
          </div>
        </div>
      </div>
    </section>
  );
};

export default ForProviders;
