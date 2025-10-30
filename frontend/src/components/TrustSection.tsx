import { Card, CardContent } from "@/components/ui/card";
import { Shield, Award, ShieldCheck, Zap } from "lucide-react";

const stats = [
  {
    icon: ShieldCheck,
    title: "100% HIPAA Compliant",
    description: "Meeting international healthcare data protection standards",
  },
  {
    icon: Award,
    title: "ISO 27001 Certified",
    description: "Information security management excellence",
  },
  {
    icon: Shield,
    title: "Zero Data Breaches",
    description: "Proven track record of securing sensitive health information",
  },
  {
    icon: Zap,
    title: "99.9% Uptime",
    description: "Always available when you need it most",
  },
];

const testimonials = [
  {
    quote: "MedVault saved me hours at my new specialist appointment. Instead of scrambling to collect reports from three different hospitals, I just shared access through the app. My doctor had my complete history instantly.",
    author: "Priya Sharma",
    role: "Patient",
  },
  {
    quote: "As an emergency physician, MedVault's emergency access protocol has been invaluable. We can access critical patient information like allergies and chronic conditions within seconds, potentially saving lives.",
    author: "Dr. Rajesh Mehta",
    role: "Emergency Medicine",
  },
];

const TrustSection = () => {
  return (
    <section className="py-20 bg-gradient-subtle">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12 space-y-3">
          <h2 className="text-3xl md:text-4xl font-bold text-foreground">
            Trusted By Patients Across the Country
          </h2>
        </div>
        
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
          {stats.map((stat, index) => (
            <Card key={index} className="text-center bg-card border-border hover:shadow-elegant transition-all duration-300">
              <CardContent className="p-6">
                <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-primary/10 flex items-center justify-center">
                  <stat.icon className="w-8 h-8 text-primary" />
                </div>
                <h3 className="text-xl font-semibold text-card-foreground mb-2">{stat.title}</h3>
                <p className="text-[1.1rem] text-muted-foreground">{stat.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="grid md:grid-cols-2 gap-8">
          {testimonials.map((testimonial, index) => (
            <Card key={index} className="bg-white border-border shadow-elegant">
              <CardContent className="p-8">
                <div className="space-y-4">
                  <div className="text-4xl text-accent">"</div>
                  <p className="text-muted-foreground italic leading-relaxed text-[1.125rem]">
                    {testimonial.quote}
                  </p>
                  <div className="pt-4 border-t border-border">
                    <p className="font-semibold text-foreground">{testimonial.author}</p>
                    <p className="text-[1.1rem] text-muted-foreground">{testimonial.role}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};

export default TrustSection;
