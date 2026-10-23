import type { Metadata } from "next";
import { SiteHeader } from "@/components/site-header";
import { PricingPlans } from "@/components/pricing-plans";
import { ComparisonTable } from "@/components/comparison-table";
import { Faq } from "@/components/faq";
import { SiteFooter } from "@/components/site-footer";

export const metadata: Metadata = {
  title: "پلن‌ها و قیمت‌ها | مکالمات من",
  description:
    "پلن رایگان، پلاس و هوشمند مکالمات من را مقایسه کنید. دانلود اکسل و متن و خلاصه‌سازی هوش مصنوعی رایگان است و کار تیمی با پلاس در دسترس است.",
};

export default function PricingPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main id="main-content" className="flex-1 py-12 sm:py-16">
        <PricingPlans />
        <div className="mt-16 sm:mt-20">
          <ComparisonTable />
        </div>
        <Faq />
      </main>
      <SiteFooter />
    </div>
  );
}
