import { Button } from "@/components/ui/button";
import { ArrowRight } from "lucide-react";
import { Link } from "react-router-dom";

const CTASection = () => {
  return (
    <section className="py-20 bg-gradient-primary">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto text-center space-y-6">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold text-white">
            Start Managing Your Health Records Today
          </h2>
          <p className="text-xl md:text-xl text-primary-light/90">
            Join thousands of patients and healthcare providers who trust MedVault for secure, interoperable health data management.
          </p>
          <div className="flex justify-center pt-4">
            <Link to="/signup">
              <Button size="lg" variant="destructive" className="group">
                Get Started Free
                <ArrowRight className="ml-2 group-hover:translate-x-1 transition-transform" />
              </Button>
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
};

export default CTASection;
