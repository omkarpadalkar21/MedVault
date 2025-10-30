import { Shield } from "lucide-react";

const footerSections = [
  {
    title: "Quick Links",
    links: ["About Us", "How It Works", "Security & Privacy", "FHIR API Documentation", "Support Center", "Blog", "Careers"],
  },
  {
    title: "For Members",
    links: ["Patient Portal", "Doctor Portal", "Healthcare Organizations", "Pricing"],
  },
  {
    title: "Legal",
    links: ["Terms of Service", "Privacy Policy", "Cookie Policy", "HIPAA Compliance", "Data Processing Agreement"],
  },
  {
    title: "Resources",
    links: ["Getting Started Guide", "Video Tutorials", "Integration Guide", "FAQ"],
  },
];

const Footer = () => {
  return (
    <footer className="bg-primary-dark text-white">
      <div className="container mx-auto px-4 py-16">
        <div className="grid md:grid-cols-2 lg:grid-cols-5 gap-8 mb-12">
          <div className="lg:col-span-1">
            <div className="flex items-center gap-2 mb-4">
              <Shield className="w-8 h-8 text-accent" />
              <span className="text-2xl font-bold">MedVault</span>
            </div>
            <p className="text-primary-light/80 text-sm leading-relaxed mb-4">
              Empowering Patients, Enabling Providers
            </p>
            <p className="text-primary-light/60 text-xs">
              India's leading interoperable EHR platform with HL7 FHIR standards and enterprise-grade security.
            </p>
          </div>
          
          {footerSections.map((section, index) => (
            <div key={index}>
              <h3 className="font-semibold mb-4 text-white">{section.title}</h3>
              <ul className="space-y-2">
                {section.links.map((link, linkIndex) => (
                  <li key={linkIndex}>
                    <a
                      href="#"
                      className="text-sm text-primary-light/70 hover:text-accent transition-colors"
                    >
                      {link}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        <div className="border-t border-white/10 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-sm text-primary-light/60">
              © {new Date().getFullYear()} MedVault. All rights reserved.
            </p>
            <div className="flex items-center gap-4">
              <span className="text-xs text-primary-light/60">
                Securing health data for over 100,000+ patients and 5,000+ healthcare providers
              </span>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
