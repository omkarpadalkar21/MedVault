import { Card, CardContent } from "@/components/ui/card";
import { Lock, Activity, CheckCircle, AlertTriangle, RefreshCw, Search, Smartphone } from "lucide-react";

const features = [
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
    <section className="py-20 bg-gradient-hero text-white">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12 space-y-3">
          <h2 className="text-3xl md:text-4xl font-bold">
            Enterprise-Grade Security Meets Patient-First Design
          </h2>
          <p className="text-xl text-primary-light/80 max-w-2xl mx-auto">
            Built with the latest security standards and designed for ease of use
          </p>
        </div>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6 place-items-center">
          {features.map((feature, index) => (
            <Card
              key={index}
              className="bg-white/10 backdrop-blur border-white/10 hover:bg-white/20 transition-all duration-300 hover:shadow-elegant-lg group"
            >
              <CardContent className="p-6">
                <div className="space-y-3 text-center flex flex-col items-center">
                  <div className="w-14 h-14 rounded-xl bg-destructive/25 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <feature.icon className="w-7 h-7 text-destructive" />
                  </div>
                  <h3 className="text-xl font-semibold text-white">{feature.title}</h3>
                  <p className="text-primary-light/90 text-sm leading-relaxed text-[1.15rem]">{feature.description}</p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};

export default Features;
