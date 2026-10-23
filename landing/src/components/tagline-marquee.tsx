"use client";

import { TAGLINE_PHRASES } from "@/lib/site";
import { Sparkle } from "@/components/reveal";

export function TaglineMarquee() {
  const items = [...TAGLINE_PHRASES, ...TAGLINE_PHRASES, ...TAGLINE_PHRASES, ...TAGLINE_PHRASES];

  return (
    <section aria-label="شعار" className="relative my-10 overflow-hidden border-y border-white/70 bg-white/55 py-4 backdrop-blur sm:my-14">
      <div className="pointer-events-none absolute inset-y-0 right-0 z-10 w-16 bg-gradient-to-l from-canvas to-transparent sm:w-28" />
      <div className="pointer-events-none absolute inset-y-0 left-0 z-10 w-16 bg-gradient-to-r from-canvas to-transparent sm:w-28" />

      <div dir="ltr" className="flex w-max animate-marquee items-center will-change-transform">
        {items.map((phrase, index) => (
          <span key={`${phrase}-${index}`} className="flex items-center">
            <span className="whitespace-nowrap px-5 text-[13px] font-medium text-ink-muted sm:text-sm">
              {phrase}
            </span>
            <Sparkle className="h-3.5 w-3.5 shrink-0 text-brand-400" />
          </span>
        ))}
      </div>
    </section>
  );
}
