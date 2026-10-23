import Link from "next/link";
import { BrandMark } from "@/components/brand";
import { FOOTER_LINKS, SITE } from "@/lib/site";

export function SiteFooter() {
  return (
    <footer className="mt-8 border-t border-white/70 bg-white/60 backdrop-blur-xl">
      <div className="section-shell py-12">
        <div className="grid gap-10 lg:grid-cols-[1.3fr_1fr]">
          <div>
            <Link href="/" className="flex items-center gap-3">
              <BrandMark className="h-12 w-12" />
              <span className="flex flex-col leading-tight">
                <span className="text-[17px] font-bold text-ink">{SITE.name}</span>
                <span className="text-[11.5px] text-ink-muted">{SITE.tagline}</span>
              </span>
            </Link>
            <p className="mt-5 max-w-md text-[13.5px] leading-7 text-ink-muted">
              {SITE.name} · {SITE.slogan}
            </p>
            <p className="mt-6 inline-flex items-center gap-2 rounded-2xl border border-brand-100 bg-brand-50/70 px-4 py-2.5 text-[12.5px] text-brand-700">
              رایگان شروع کنید؛ بدون کارت بانکی.
            </p>
          </div>

          <nav aria-label="پیوندهای پانویس">
            <h2 className="text-[13px] font-bold text-ink">دسترسی سریع</h2>
            <ul className="mt-4 grid gap-2.5 sm:grid-cols-2">
              {FOOTER_LINKS.map((link) => (
                <li key={link.href}>
                  <Link
                    href={link.href}
                    className="text-[13.5px] text-ink-muted transition hover:text-brand-600"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </nav>
        </div>

        <div className="mt-10 flex flex-col items-center justify-between gap-3 border-t border-slate-200/70 pt-6 sm:flex-row">
          <p className="text-[12.5px] text-ink-faint">{SITE.name} · {SITE.tagline}</p>
          <p className="text-[12.5px] text-ink-faint">ساخته‌شده برای گفتگوهای ماندگار</p>
        </div>
      </div>
    </footer>
  );
}
