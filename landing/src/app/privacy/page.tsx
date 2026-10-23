import type { Metadata } from "next";
import { PageShell } from "@/components/page-shell";

export const metadata: Metadata = {
  title: "حریم خصوصی | مکالمات من",
  description: "خلاصهٔ روشن از نحوهٔ نگهداری و همگام‌سازی داده‌های شما در مکالمات من.",
};

const SECTIONS = [
  {
    title: "دفتر شخصی شما خصوصی است",
    body:
      "گزاره‌ها و یادداشت‌های دفتر شخصی شما برای دیگران نمایش داده نمی‌شود. فقط گزاره‌هایی که خودتان در پروندهٔ مشترک تیم منتشر می‌کنید با اعضای پذیرفته‌شده به اشتراک گذاشته می‌شوند.",
  },
  {
    title: "ذخیرهٔ آفلاین روی گوشی",
    body:
      "در نسخهٔ اندروید، تغییرات ابتدا روی همان گوشی ذخیره می‌شوند و پس از اتصال به اینترنت، با حساب شما همگام‌سازی می‌شوند.",
  },
  {
    title: "اشتراک‌گذاری اختیاری",
    body:
      "هیچ گزاره‌ای بدون تصمیم شما با تیم به اشتراک گذاشته نمی‌شود و با پایان اشتراک، دسترسی مشترک تا تمدید متوقف می‌شود.",
  },
];

export default function PrivacyPage() {
  return (
    <PageShell
      kicker="حریم خصوصی"
      title="چه چیزی محفوظ می‌ماند و چه چیزی به اشتراک گذاشته می‌شود."
      intro="این صفحه خلاصه‌ای روشن از رفتار برنامه در نگهداری داده‌هاست؛ بدون پیچیدگی حقوقی."
    >
      <div className="flex flex-col gap-4">
        {SECTIONS.map((section) => (
          <div key={section.title} className="card p-6">
            <h2 className="text-[15px] font-bold text-ink">{section.title}</h2>
            <p className="mt-3 text-[13.5px] leading-8 text-ink-muted">{section.body}</p>
          </div>
        ))}
      </div>
    </PageShell>
  );
}
