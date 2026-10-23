export function BrandMark({ className = "h-11 w-11" }: { className?: string }) {
  return (
    <span
      className={`relative inline-flex shrink-0 items-center justify-center overflow-hidden rounded-2xl bg-gradient-to-br from-brand-400 via-brand-500 to-brand-700 shadow-glow ${className}`}
      aria-hidden="true"
    >
      <svg viewBox="0 0 48 48" className="h-[62%] w-[62%]">
        <path
          fill="#fff"
          d="M24 8.5c-8.6 0-15.5 5.6-15.5 12.6 0 4 2.2 7.5 5.7 9.8-.3 2.4-1.4 4.4-3 5.8-.5.4-.2 1.2.5 1.1 3.6-.4 6.6-1.8 8.8-3.5 1.1.2 2.3.3 3.5.3 8.6 0 15.5-5.6 15.5-12.5S32.6 8.5 24 8.5z"
          opacity="0.96"
        />
        <circle cx="17.5" cy="21" r="2.1" fill="#229ed9" />
        <circle cx="24" cy="21" r="2.1" fill="#229ed9" />
        <circle cx="30.5" cy="21" r="2.1" fill="#229ed9" />
      </svg>
    </span>
  );
}

export function BrandLock({ subtitle = "دفتر گفتگوهای شما" }: { subtitle?: string }) {
  return (
    <span className="flex items-center gap-3">
      <BrandMark />
      <span className="flex flex-col leading-tight">
        <span className="text-[17px] font-bold tracking-tight text-ink">مکالمات من</span>
        <span className="text-[11.5px] font-medium text-ink-muted">{subtitle}</span>
      </span>
    </span>
  );
}
