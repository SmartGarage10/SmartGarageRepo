"use client";

import { Button } from "@/components/ui/button";
import { useState } from "react";

const slides = [
    {
        src: "https://images.unsplash.com/photo-1487754180451-c456f719a1fc?q=80&w=400&auto=format&fit=crop",
        alt: "Engine repair",
        title: "Engine & Mechanical Repairs",
        description:
            "From engine diagnostics to full mechanical repairs, our technicians work together to ensure accurate fixes and long-lasting performance.",
        buttonText: "View Repairs",
        buttonUrl: "#services",
    },
    {
        src: "https://images.unsplash.com/photo-1625047509168-a7026f36de04?q=80&w=400&auto=format&fit=crop",
        alt: "Diagnostics",
        title: "Advanced Diagnostics",
        description:
            "We use professional diagnostic equipment to quickly identify faults and prevent costly breakdowns.",
        buttonText: "Diagnostics",
        buttonUrl: "#services",
    }
];

const defaultAchievements = [
    { label: "Vehicles Serviced", value: "15,000+" },
    { label: "Happy Customers", value: "98.7%" },
    { label: "Certified Techs", value: "25+" },
    { label: "Years Experience", value: "20+" },
];

const About3 = ({
                    title = "About Our Car Service",
                    description =
                    "Our experienced mechanics work as a team to provide reliable diagnostics, professional repairs, and quality maintenance services.",

                    mainImage = {
                        src: "https://images.openai.com/static-rsc-4/z527XXNlXVPPB8v2n5JLdjJicD9AsznO8SGJ6zioteXZ4yyBk90ZlbtEsBGoUVQg7Mqz75ugUpvwAVyLHRHqf2xmS4vv8eU-3Ratk7GEQreoOqG5Pn6rka2eAv3jAkuDYOjP75ru5m_I72njeTPMjlLiWI3MCous9ZhIpqaWK1L6we437izihTwNxTCnCk_b?purpose=inline",
                        alt: "Mechanic team working on car engines",
                    },

                    secondaryImage = {
                        src: "https://images.openai.com/static-rsc-4/IIFpUExUKskpyfQzyiTa6gM2CZozFKRsbqwD4OAfBYCWeHy0fLjYB-VzgQcIx5nR8Y8uH5iohI_dKgvmXJuW2AKNzxtvUcgeE2uEPIbd1s6AFPG8FAbGs6mOwq4oqymbse-j1xAkmqDRIw-xOBRNBhBOvo9__9dh3lyA2PW0cl6xdD5PMJ1eJJDjmyOdwr7M?purpose=fullsize",
                        alt: "Mechanics performing diagnostics together",
                    },

                    achievementsTitle = "Why Drivers Trust Us",
                    achievementsDescription =
                    "Professional teamwork, modern equipment, and honest service you can rely on.",
                    achievements = defaultAchievements,
                } = {}) => {
    const [active, setActive] = useState(0);

    const next = () => setActive((prev) => (prev + 1) % slides.length);
    const prev = () =>
        setActive((prev) => (prev - 1 + slides.length) % slides.length);

    const slide = slides[active];

    return (
        <section className="py-32 bg-black">
            <div className="container">
                <div className="mb-14 grid gap-5 text-center md:grid-cols-2 md:text-left">
                    <h1 className="text-5xl font-semibold text-white">{title}</h1>
                    <p className="text-gray-300">{description}</p>
                </div>

                <div className="grid gap-7 lg:grid-cols-3">
                    <img
                        src={mainImage.src}
                        alt={mainImage.alt}
                        className="size-full max-h-[620px] rounded-xl object-cover lg:col-span-2 border border-lime-500/20
                                   shadow-[0_0_15px_rgba(132,204,22,0.4)] hover:shadow-[0_0_25px_rgba(132,204,22,0.6)] transition-shadow duration-300"
                    />

                    <div className="flex flex-col gap-7 md:flex-row lg:flex-col">
                        {/* Slider card with lime glow */}
                        <div className="flex flex-col justify-between h-[320px] rounded-xl bg-gradient-to-br from-gray-900 to-black p-7 md:w-1/2 lg:w-auto border border-lime-500/20
                                        shadow-[0_0_10px_rgba(132,204,22,0.5)] hover:shadow-[0_0_20px_rgba(132,204,22,0.7)] transition-shadow duration-300">
                            <img
                                src={slide.src}
                                alt={slide.alt}
                                className="h-12 w-12 rounded-full object-cover"
                            />

                            <div className="flex-1 pt-2">
                                <p className="mb-2 text-lg font-semibold text-white">
                                    {slide.title}
                                </p>
                                <p className="text-gray-300 text-sm leading-relaxed">
                                    {slide.description}
                                </p>
                            </div>

                            <div className="flex items-center justify-between gap-3 pt-2">
                                <Button
                                    variant="outline"
                                    className="border-lime-500/40 bg-transparent text-lime-400 hover:bg-lime-500/10 hover:text-lime-300"
                                    asChild
                                >
                                    <a href={slide.buttonUrl}>{slide.buttonText}</a>
                                </Button>

                                <div className="flex gap-2">
                                    <button
                                        onClick={prev}
                                        className="px-3 py-1 text-sm border border-lime-500/30 rounded-md text-lime-400 hover:bg-lime-500/10"
                                    >
                                        ←
                                    </button>
                                    <button
                                        onClick={next}
                                        className="px-3 py-1 text-sm border border-lime-500/30 rounded-md text-lime-400 hover:bg-lime-500/10"
                                    >
                                        →
                                    </button>
                                </div>
                            </div>
                        </div>

                        <img
                            src={secondaryImage.src}
                            alt={secondaryImage.alt}
                            className="grow basis-0 rounded-xl object-cover md:w-1/2 lg:min-h-0 lg:w-auto border border-lime-500/20
                                       shadow-[0_0_15px_rgba(132,204,22,0.4)] hover:shadow-[0_0_25px_rgba(132,204,22,0.6)] transition-shadow duration-300"
                        />
                    </div>
                </div>

                <div className="relative overflow-hidden rounded-xl bg-gradient-to-br from-gray-900 to-black p-10 md:p-16 my-5 border border-lime-500/20
                                shadow-[0_0_15px_rgba(132,204,22,0.3)] hover:shadow-[0_0_30px_rgba(132,204,22,0.5)] transition-shadow duration-300">
                    <div className="flex flex-col gap-4 text-center md:text-left">
                        <h2 className="text-4xl font-semibold text-white">
                            {achievementsTitle}
                        </h2>
                        <p className="max-w-xl text-gray-300">{achievementsDescription}</p>
                    </div>

                    <div className="mt-10 flex flex-wrap justify-between gap-10 text-center">
                        {achievements.map((item, idx) => (
                            <div key={idx} className="flex flex-col gap-4">
                                <p className="text-gray-300">{item.label}</p>
                                <span className="text-4xl font-semibold md:text-5xl text-lime-400">
                                    {item.value}
                                </span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </section>
    );
};

export { About3 };