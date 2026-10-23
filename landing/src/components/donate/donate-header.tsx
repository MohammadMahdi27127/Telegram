import Link from "next/link";
import { ArrowUpLeft } from "lucide-react";
import { BrandMark } from "@/components/brand";

export function DonateHeader() {
  return (
    <header className="sticky top-0 z-50 border-b border-white/70 bg-white/75 backdrop-blur-xl">
      <div className="mx-auto flex h-[70px] w-full max-w-5xl items-center justify-between gap-4 px-4 sm:px-6">
        <Link href="/" aria-label="مکالمات من" className="flex items-center gap-3">
          <BrandMark className="h-10 w-10" />
          <span className="text-[16px] font-bold tracking-tight text-ink">مکالمات من</span>
        </Link>

        <Link
          href="/"
          className="inline-flex items-center gap-1.5 rounded-2xl border border-slate-200 bg-white/80 px-4 py-2.5 text-[12.5px] font-medium text-ink-soft transition hover:-translate-y-0.5 hover:border-brand-200 hover:text-brand-700"
        >
          بازگشت به سایت
          <ArrowUpLeft className="h-3.5 w-3.5" />
        </Link>
      </div>
    </header>
  );
}
