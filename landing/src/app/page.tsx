import { SiteHeader } from "@/components/site-header";
import { Hero } from "@/components/hero";
import { TaglineMarquee } from "@/components/tagline-marquee";
import { Features } from "@/components/features";
import { PlusShowcase } from "@/components/plus-showcase";
import { Faq } from "@/components/faq";
import { DownloadCta } from "@/components/download-cta";
import { SiteFooter } from "@/components/site-footer";

export default function HomePage() {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main id="main-content" className="flex-1">
        <Hero />
        <TaglineMarquee />
        <Features />
        <PlusShowcase />
        <Faq />
        <DownloadCta />
      </main>
      <SiteFooter />
    </div>
  );
}
