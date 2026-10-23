"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { Download, Menu, X } from "lucide-react";
import { BrandLock } from "@/components/brand";
import { NAV_LINKS, SITE } from "@/lib/site";

export function SiteHeader() {
  const [open, setOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const pathname = usePathname();

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 12);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <header className="sticky top-0 z-50 w-full">
      <div
        className={`w-full transition-all duration-300 ${
          scrolled ? "border-b border-white/60 bg-white/80 backdrop-blur-xl shadow-soft" : "bg-transparent"
        }`}
      >
        <div className="section-shell flex h-[74px] items-center justify-between gap-4">
          <Link href="/" aria-label="مکالمات من" className="transition hover:opacity-90">
            <BrandLock />
          </Link>

          <nav className="hidden items-center gap-1 lg:flex" aria-label="ناوبری اصلی">
            {NAV_LINKS.map((link) => {
              const active = pathname === link.href;
              return (
                <Link
                  key={link.href}
                  href={link.href}
                  aria-current={active ? "page" : undefined}
                  className={`rounded-xl px-4 py-2 text-sm transition ${
                    active
                      ? "bg-brand-50 font-semibold text-brand-700"
                      : "font-medium text-ink-soft hover:bg-brand-50 hover:text-brand-700"
                  }`}
                >
                  {link.label}
                </Link>
              );
            })}
          </nav>

          <div className="flex items-center gap-2">
            <a href={SITE.androidApk} download className="btn-primary hidden !px-5 !py-2.5 lg:inline-flex">
              <Download className="h-4 w-4" />
              دریافت مکالمات من
            </a>
            <button
              type="button"
              onClick={() => setOpen((v) => !v)}
              aria-label="منوی موبایل"
              aria-expanded={open}
              className="inline-flex h-11 w-11 items-center justify-center rounded-2xl border border-slate-200 bg-white/80 text-ink-soft backdrop-blur transition hover:border-brand-200 hover:text-brand-700 lg:hidden"
            >
              {open ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
            </button>
          </div>
        </div>
      </div>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            transition={{ duration: 0.2 }}
            className="section-shell lg:hidden"
          >
            <div className="mt-2 rounded-3xl border border-white/70 bg-white/90 p-3 shadow-lift backdrop-blur-xl">
              <nav className="flex flex-col" aria-label="ناوبری موبایل">
                {NAV_LINKS.map((link) => {
                  const active = pathname === link.href;
                  return (
                    <Link
                      key={link.href}
                      href={link.href}
                      onClick={() => setOpen(false)}
                      aria-current={active ? "page" : undefined}
                      className={`rounded-2xl px-4 py-3 text-sm transition ${
                        active
                          ? "bg-brand-50 font-semibold text-brand-700"
                          : "font-medium text-ink-soft hover:bg-brand-50 hover:text-brand-700"
                      }`}
                    >
                      {link.label}
                    </Link>
                  );
                })}
              </nav>
              <a
                href={SITE.androidApk}
                download
                onClick={() => setOpen(false)}
                className="btn-primary mt-2 w-full"
              >
                <Download className="h-4 w-4" />
                دریافت مکالمات من
              </a>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </header>
  );
}
