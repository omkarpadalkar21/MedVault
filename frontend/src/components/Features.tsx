import { Card } from "@/components/ui/card";
import { Lock, Activity, CheckCircle, AlertTriangle, RefreshCw, Search, Smartphone } from "lucide-react";

const features = [
  {
    icon: Lock,
    title: "End-to-End Encryption",
    description: "Your medical data is encrypted at rest and in transit using military-grade AES-256 encryption",
  },
  {
    icon: Activity,
    title: "HL7 FHIR Standard",
    description: "Full compliance with international healthcare data exchange standards for seamless interoperability",
  },
  {
    icon: CheckCircle,
    title: "Consent Management",
    description: "Granular control over who can access your data, for how long, and what they can view",
  },
  {
    icon: AlertTriangle,
    title: "Emergency Access",
    description: "Life-saving protocols allow verified doctors to access critical information during emergencies",
  },
  {
    icon: RefreshCw,
    title: "Real-Time Sync",
    description: "Updates across all healthcare providers instantly with cloud-based infrastructure",
  },
  {
    icon: Search,
    title: "AI Anomaly Detection",
    description: "Advanced AI monitors suspicious access patterns to protect your privacy",
  },
  {
    icon: Smartphone,
    title: "Multi-Platform Access",
    description: "Access your health records from web, mobile, or tablet—anytime, anywhere",
  },
];

const Features = () => {
  return (
    <section className="py-20 bg-primary-dark text-white">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12 space-y-3">
          <h2 className="text-3xl md:text-4xl font-bold">
            Enterprise-Grade Security Meets Patient-First Design
          </h2>
          <p className="text-lg text-primary-light/80 max-w-2xl mx-auto">
            Built with the latest security standards and designed for ease of use
          </p>
        </div>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((feature, index) => (
            <Card
              key={index}
              className="p-6 bg-white/5 backdrop-blur-sm border-white/10 hover:bg-white/10 transition-all duration-300 hover:shadow-elegant-lg group"
            >
              <div className="space-y-3">
                <div className="w-14 h-14 rounded-xl bg-accent/20 flex items-center justify-center group-hover:scale-110 transition-transform">
                  <feature.icon className="w-7 h-7 text-accent" />
                </div>
                <h3 className="text-xl font-semibold text-white">{feature.title}</h3>
                <p className="text-primary-light/70 text-sm leading-relaxed">{feature.description}</p>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};

export default Features;
