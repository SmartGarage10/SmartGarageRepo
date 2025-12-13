import React from "react";
import HeroSection from "@/components/home-components/hero-section";
import { About3 } from "@/components/home-components/about3";
import WorkSection from "@/components/home-components/work";
import Contact from "@/components/home-components/contact-02";
import { Footer2 } from "@/components/home-components/footer2";
import Service  from "@/components/home-components/service";
import Pricing04 from "@/components/home-components/pricing";
import FAQ02 from "@/components/home-components/faq-02";


export default function Home() {
  return (
      <main className="flex flex-col items-center justify-between">
          <HeroSection />
          <About3 />
          <WorkSection />
          <Service />
          <Pricing04 />
          <FAQ02 />
          <Contact />
          <Footer2 />
    </main>
  );
}
