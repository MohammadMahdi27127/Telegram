import type { Metadata } from "next";
import Link from "next/link";
import { Info } from "lucide-react";
import { DonateHeader } from "@/components/donate/donate-header";
import { BankCard } from "@/components/donate/bank-card";
import { TrackingCard } from "@/components/donate/tracking-card";
import { Reveal } from "@/components/reveal";

export const metadata: Metadata = {
  title: "حمایت اختیاری | مکالمات من",
  description:
    "اگر مکالمات من برایتان مفید بوده، می‌توانید با هر مبلغی که دوست دارید از ادامهٔ توسعهٔ آن حمایت کنید.",
};

export default function DonatePage() {
  return (
    <div className="flex min-h-screen flex-col">
      <DonateHeader />

      <main id="main-content" className="flex-1 py-10 sm:py-14">
        <div className="mx-auto w-full max-w-5xl px-4">
          {/* هیرو */}
          <Reveal>
            <div className="text-center">
              <span className="inline-flex items-center gap-2 rounded-full border border-rose-100 bg-rose-50/80 px-4 py-1.5 text-[12px] font-medium text-rose-600">
                یک همراهیِ اختیاری
              </span>
              <h1 className="heading-lg mt-5 text-ink">
                با همراهی شما،
                <br />
                مکالمات من بهتر می‌شود.
              </h1>
              <p className="mx-auto mt-4 max-w-2xl text-[14px] leading-8 text-ink-soft sm:text-[14.5px]">
                اگر مکالمات من برایتان مفید بوده، می‌توانید با هر مبلغی که دوست دارید از ادامهٔ توسعهٔ آن حمایت
                کنید.
              </p>
            </div>
          </Reveal>

          {/* پیام شفاف‌سازی */}
          <Reveal delay={0.08}>
            <div className="mx-auto mt-7 flex max-w-2xl items-start gap-3 rounded-3xl border border-brand-100 bg-brand-50/70 p-4 sm:p-5">
              <span className="inline-flex h-9 w-9 shrink-0 items-center justify-center rounded-2xl bg-white text-brand-600 shadow-soft">
                <Info className="h-4 w-4" />
              </span>
              <p className="text-[13px] leading-7 text-brand-800">
                حمایت کاملاً اختیاری است و هیچ اشتراک، اعتبار یا امکان تازه‌ای فعال نمی‌کند.
              </p>
            </div>
          </Reveal>

          {/* گرید دوستونه */}
          <div className="mt-10 grid grid-cols-1 items-stretch gap-6 md:grid-cols-2">
            <Reveal delay={0.12} className="flex">
              <div className="flex w-full">
                <BankCard />
              </div>
            </Reveal>
            <Reveal delay={0.18} className="flex">
              <div className="flex w-full">
                <TrackingCard />
              </div>
            </Reveal>
          </div>
        </div>
      </main>

      <footer className="mt-6 border-t border-white/70 bg-white/60 backdrop-blur-xl">
        <div className="mx-auto flex w-full max-w-5xl flex-col items-center justify-between gap-4 px-4 py-8 sm:flex-row sm:px-6">
          <p className="text-[13px] font-medium text-ink-soft">با سپاس از همراهی شما</p>
          <nav aria-label="پیوندهای پانویس" className="flex items-center gap-3 text-[12.5px]">
            <Link href="/privacy" className="text-ink-muted transition hover:text-brand-600">
              حریم خصوصی
            </Link>
            <span className="text-ink-faint" aria-hidden="true">
              |
            </span>
            <Link href="/" className="text-ink-muted transition hover:text-brand-600">
              مکالمات من
            </Link>
          </nav>
        </div>
      </footer>
    </div>
  );
}
