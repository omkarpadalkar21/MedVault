import Navbar from "@/components/Navbar";
import Hero from "@/components/Hero";
import ForPatients from "@/components/ForPatients";
import ForProviders from "@/components/ForProviders";
import Features from "@/components/Features";
import TrustSection from "@/components/TrustSection";
import CTASection from "@/components/CTASection";
import Footer from "@/components/Footer";

const Index = () => {
  return (
    <div className="min-h-screen">
      <Navbar />
      <Hero />
      <ForPatients />
      <ForProviders />
      <Features />
      <TrustSection />
      <CTASection />
      <Footer />
    </div>
  );
};

export default Index;
