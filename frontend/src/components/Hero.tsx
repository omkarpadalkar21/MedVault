import { Button } from "@/components/ui/button";
import { ArrowRight, Shield, Users } from "lucide-react";
import heroImage from "@/assets/hero-medical.jpg";

const Hero = () => {
  return (
    <section className="relative bg-gradient-hero overflow-hidden">
      <div className="container mx-auto px-4 py-20 md:py-32">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div className="text-white space-y-6 animate-fade-in">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold leading-tight">
              Secure Your Health Records, Anytime, Anywhere
            </h1>
            <p className="text-lg md:text-xl text-primary-light/90">
              Take control of your medical data with MedVault. Access, share, and manage your health information securely with complete privacy, interoperability, and consent-based sharing across all healthcare providers.
            </p>
            <div className="flex flex-wrap gap-4 pt-4">
              <Button size="lg" variant="accent" className="group">
                Get Started Free
                <ArrowRight className="ml-2 group-hover:translate-x-1 transition-transform" />
              </Button>
              <Button size="lg" variant="outline" className="bg-white/10 border-white/30 text-white hover:bg-white/20 backdrop-blur-sm">
                Learn How It Works
              </Button>
            </div>
            <div className="flex items-center gap-8 pt-6">
              <div className="flex items-center gap-2">
                <Shield className="w-5 h-5 text-accent" />
                <span className="text-sm text-primary-light/90">HIPAA Compliant</span>
              </div>
              <div className="flex items-center gap-2">
                <Users className="w-5 h-5 text-accent" />
                <span className="text-sm text-primary-light/90">100,000+ Patients</span>
              </div>
            </div>
          </div>
          <div className="relative animate-slide-in-right">
            <div className="relative rounded-2xl overflow-hidden shadow-2xl">
              <img
                src={heroImage}
                alt="Healthcare professionals collaborating with digital health technology"
                className="w-full h-auto"
              />
            </div>
            <div className="absolute -bottom-6 -left-6 bg-white rounded-xl p-4 shadow-elegant-lg animate-float">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-primary/10 rounded-full flex items-center justify-center">
                  <Shield className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <p className="text-sm font-semibold text-foreground">100% Secure</p>
                  <p className="text-xs text-muted-foreground">End-to-end encrypted</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Hero;
