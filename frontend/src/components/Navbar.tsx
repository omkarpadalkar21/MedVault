import { Button } from "@/components/ui/button";
import { Shield, Menu } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router-dom";

const Navbar = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <nav className="bg-white border-b border-border sticky top-0 z-50 backdrop-blur-sm bg-white/95">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-2">
            <Shield className="w-8 h-8 text-destructive" />
            <span className="text-xl font-bold text-foreground">MedVault</span>
          </div>
          
          <div className="hidden md:flex items-center gap-8">
            <a href="#features" className="text-lg font-medium text-foreground hover:text-primary transition-colors">
              Features
            </a>
            <a href="#for-patients" className="text-lg font-medium text-foreground hover:text-primary transition-colors">
              For Patients
            </a>
            <a href="#for-providers" className="text-lg font-medium text-foreground hover:text-primary transition-colors">
              For Providers
            </a>
            <a href="#security" className="text-lg font-medium text-foreground hover:text-primary transition-colors">
              Security
            </a>
          </div>

          <div className="hidden md:flex items-center gap-3">
            <Link to="/login">
              <Button variant="ghost">Sign In</Button>
            </Link>
            <Link to="/signup">
              <Button variant="default">Sign Up</Button>
            </Link>
          </div>

          <button
            className="md:hidden p-2"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            <Menu className="w-6 h-6 text-foreground" />
          </button>
        </div>

        {mobileMenuOpen && (
          <div className="md:hidden py-4 space-y-3 border-t border-border">
            <a href="#features" className="block text-sm font-medium text-foreground hover:text-primary transition-colors py-2">
              Features
            </a>
            <a href="#for-patients" className="block text-sm font-medium text-foreground hover:text-primary transition-colors py-2">
              For Patients
            </a>
            <a href="#for-providers" className="block text-sm font-medium text-foreground hover:text-primary transition-colors py-2">
              For Providers
            </a>
            <a href="#security" className="block text-sm font-medium text-foreground hover:text-primary transition-colors py-2">
              Security
            </a>
            <div className="pt-3 space-y-2">
              <Link to="/login" className="block">
                <Button variant="ghost" className="w-full">Sign In</Button>
              </Link>
              <Link to="/signup" className="block">
                <Button variant="default" className="w-full">Sign Up</Button>
              </Link>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
