import type { ReactNode } from "react";
import Link from "next/link";
import { ArrowRight } from "lucide-react";
import { SiteHeader } from "@/components/site-header";
import { SiteFooter } from "@/components/site-footer";

export function PageShell({
  kicker,
  title,
  intro,
  children,
}: {
  kicker: string;
  title: string;
  intro?: string;
  children?: ReactNode;
}) {
  return (
    <div className="flex min-h-screen flex-col">
      <SiteHeader />
      <main id="main-content" className="flex-1 py-12 sm:py-16">
        <div className="section-shell">
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-[12.5px] font-medium text-ink-muted transition hover:text-brand-600"
          >
            <ArrowRight className="h-3.5 w-3.5" />
            بازگشت به صفحهٔ اصلی
          </Link>

          <div className="mt-6">
            <span className="kicker">{kicker}</span>
            <h1 className="heading-lg mt-5 text-ink">{title}</h1>
            {intro && <p className="mt-4 max-w-2xl text-[14.5px] leading-8 text-ink-soft">{intro}</p>}
          </div>

          <div className="mt-10">{children}</div>
        </div>
      </main>
      <SiteFooter />
    </div>
  );
}
