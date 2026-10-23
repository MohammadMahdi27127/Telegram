import type { Metadata, Viewport } from "next";
import localFont from "next/font/local";
import "./globals.css";

const vazirmatn = localFont({
  src: [
    { path: "./fonts/Vazirmatn-Regular.ttf", weight: "400", style: "normal" },
    { path: "./fonts/Vazirmatn-Medium.ttf", weight: "500", style: "normal" },
    { path: "./fonts/Vazirmatn-Bold.ttf", weight: "700", style: "normal" },
  ],
  variable: "--font-vazirmatn",
  display: "swap",
});

export const metadata: Metadata = {
  title: "مکالمات من | دفتر گفتگوهای شما",
  description:
    "گفتگوها، یادداشت‌ها و تصمیم‌های مهمتان را یک‌جا نگه دارید؛ از دفتر شخصی تا پرونده‌های مشترک تیم. رایگان شروع کنید و گزارش‌ها را به صورت اکسل و متن دریافت کنید.",
  keywords: ["مکالمات من", "دفتر گفتگو", "یادداشت گفتگو", "مدیریت مکالمات", "پروندهٔ گفتگو"],
  openGraph: {
    title: "مکالمات من | دفتر گفتگوهای شما",
    description: "گفتگوها می‌گذرند. مکالمات من می‌ماند.",
    type: "website",
    locale: "fa_IR",
  },
};

export const viewport: Viewport = {
  themeColor: "#f4f7fd",
};

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="fa" dir="rtl" className={vazirmatn.variable}>
      <body className="min-h-screen font-sans">{children}</body>
    </html>
  );
}
