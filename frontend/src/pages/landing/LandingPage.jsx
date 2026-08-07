import Navbar from "../../components/landing/Navbar/Navbar";
import HeroSection from "../../components/landing/Hero/HeroSection";
import FeaturesSection from "../../components/landing/Features/FeaturesSection";
import HowItWorksSection from "../../components/landing/HowItWorks/HowItWorksSection";
import CTASection from "../../components/landing/CTASection/CTASection";
import Footer from "../../components/landing/Footer/Footer";

function LandingPage() {
    return (
        <main className="min-h-screen overflow-x-clip bg-[#f7fafb] pt-20">
            <Navbar />
            <HeroSection />
            <FeaturesSection />
            <HowItWorksSection />
            <CTASection />
            <Footer />
        </main>
    );
}

export default LandingPage;
