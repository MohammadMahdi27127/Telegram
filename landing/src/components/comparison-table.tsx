import { Reveal } from "@/components/reveal";
import { COMPARISON_ROWS } from "@/lib/site";

const COLUMNS = ["رایگان", "پلاس", "هوشمند · به‌زودی"];

export function ComparisonTable() {
  return (
    <div className="section-shell">
      <Reveal className="text-center">
        <span className="kicker">انتخاب روشن، بدون حدس و گمان</span>
        <h2 className="heading-lg mt-5 text-ink">
          چه چیزی
          <span className="text-brand-600"> دریافت می‌کنید؟</span>
        </h2>
        <p className="mx-auto mt-4 max-w-xl text-[14px] leading-7 text-ink-muted">
          امکانات پلن‌های مکالمات من
        </p>
      </Reveal>

      <Reveal delay={0.08} className="mt-10">
        <div className="overflow-hidden rounded-[28px] border border-slate-200/70 bg-white/85 shadow-soft backdrop-blur">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[640px] border-collapse text-right">
              <thead>
                <tr className="bg-canvas/80">
                  <th className="px-5 py-4 text-[13px] font-bold text-ink">امکانات</th>
                  {COLUMNS.map((column, index) => (
                    <th
                      key={column}
                      className={`px-5 py-4 text-center text-[13px] font-bold ${
                        index === 1 ? "text-brand-700" : "text-ink-soft"
                      }`}
                    >
                      {column}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {COMPARISON_ROWS.map((row, index) => (
                  <tr
                    key={row.label}
                    className={index % 2 === 1 ? "bg-canvas/40" : "bg-transparent"}
                  >
                    <td className="border-t border-slate-200/70 px-5 py-4 text-[13px] font-medium text-ink">
                      {row.label}
                    </td>
                    <td className="border-t border-slate-200/70 px-5 py-4 text-center text-[13px] text-ink-muted">
                      {row.free}
                    </td>
                    <td className="border-t border-slate-200/70 px-5 py-4 text-center text-[13px] font-semibold text-brand-700">
                      {row.plus}
                    </td>
                    <td className="border-t border-slate-200/70 px-5 py-4 text-center text-[13px] text-ink-muted">
                      {row.smart}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </Reveal>
    </div>
  );
}
