"use client";

import { CloudOff, NotebookPen, UsersRound } from "lucide-react";
import { Reveal } from "@/components/reveal";
import { ThreadPreview } from "@/components/chat-mockup";
import { FEATURES } from "@/lib/site";

const ICONS = [NotebookPen, UsersRound, CloudOff];

export function Features() {
  return (
    <section id="features" className="scroll-mt-24 py-6 sm:py-10">
      <div className="section-shell">
        <Reveal className="text-center">
          <span className="kicker">از گفتگو تا یک دفتر منظم</span>
          <h2 className="heading-lg mt-5 text-ink">
            کمتر دنبال بگردید.
            <br />
            <span className="text-brand-600">بیشتر به خاطر بسپارید.</span>
          </h2>
          <p className="mx-auto mt-4 max-w-xl text-[14.5px] leading-7 text-ink-muted">
            هر گفتگو در جای خودش؛ هر یادداشت در دسترس شما.
          </p>
        </Reveal>

        <div className="mt-12 grid gap-5 md:grid-cols-3">
          {FEATURES.map((feature, index) => {
            const Icon = ICONS[index] ?? NotebookPen;
            return (
              <Reveal key={feature.index} delay={index * 0.08}>
                <article className="card group h-full p-6 transition-all duration-300 hover:-translate-y-1.5 hover:border-brand-200 hover:shadow-lift">
                  <div className="flex items-center justify-between">
                    <span className="inline-flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-50 to-brand-100 text-brand-600 transition group-hover:from-brand-500 group-hover:to-brand-600 group-hover:text-white">
                      <Icon className="h-[22px] w-[22px]" />
                    </span>
                    <span className="text-[22px] font-bold text-slate-200 transition group-hover:text-brand-200">
                      {feature.index}
                    </span>
                  </div>
                  <h3 className="mt-5 text-[17px] font-bold text-ink">{feature.title}</h3>
                  <p className="mt-3 text-[13.5px] leading-7 text-ink-muted">{feature.body}</p>
                </article>
              </Reveal>
            );
          })}
        </div>

        <Reveal delay={0.1} className="mt-12">
          <div className="rounded-[36px] border border-white/70 bg-white/60 p-5 shadow-soft backdrop-blur-xl sm:p-8">
            <div className="grid items-center gap-8 lg:grid-cols-2 lg:gap-12">
              <div>
                <span className="kicker">دفتر گفتگوها</span>
                <h3 className="mt-5 text-2xl font-bold leading-[1.5] text-ink sm:text-[28px]">
                  همه‌چیز، مرتب و در دسترس؛
                  <br />
                  <span className="text-brand-600">بدون گشتن بین کاغذها.</span>
                </h3>
                <p className="mt-4 text-[14px] leading-7 text-ink-muted">
                  گزاره‌ها، یادداشت‌ها و پیام‌های صوتی هر فرد را در یک گفتگوی روشن ببینید؛ با برچسب دسته‌بندی،
                  جستجوی سریع و خلاصهٔ هوشمند.
                </p>
              </div>
              <ThreadPreview />
            </div>
          </div>
        </Reveal>
      </div>
    </section>
  );
}
