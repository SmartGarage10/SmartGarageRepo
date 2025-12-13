import React from 'react'
import Link from 'next/link'
import { ArrowRight } from 'lucide-react'
import { Button } from '@/components/ui/button'
import Image from 'next/image'
import { TextEffect } from '@/components/motion-primitives/text-effect'
import { AnimatedGroup } from '@/components/motion-primitives/animated-group'
import { HeroHeader } from "@/components/home-components/header"

const transitionVariants = {
    item: {
        hidden: {
            opacity: 0,
            filter: 'blur(12px)',
            y: 12,
        },
        visible: {
            opacity: 1,
            filter: 'blur(0px)',
            y: 0,
            transition: {
                type: 'spring',
                bounce: 0.3,
                duration: 1.5,
            },
        },
    },
}

export default function HeroSection() {
    return (
        <>
            <HeroHeader />

            {/* Full Screen Hero Section */}
            <section className="relative h-screen w-full">
                {/* Background Image - Full Screen */}
                <Image
                    src="/hero-section-bg.jpg"
                    alt="Apex Auto Service Center"
                    fill
                    className="object-cover object-center"  // Add object-center
                    priority
                    sizes="100vw"
                    style={{
                        objectPosition: 'center', // Explicit positioning
                    }}
                />

                {/* Dark Overlay for Text Readability */}
                <div className="absolute inset-0 bg-black/40"></div>

                {/* Content Centered */}
                <div className="relative z-10 h-full flex items-center justify-center">
                    <div className="text-center max-w-7xl mx-auto px-6">
                        <AnimatedGroup variants={transitionVariants}>
                            <Link
                                href="#link"
                                className="bg-white/90 group mx-auto flex w-fit items-center gap-4 rounded-full border p-1 pl-4 shadow-lg transition-colors duration-300">
                                <span className="text-black text-sm font-medium">Same-day repairs now available</span>
                                <span className="block h-4 w-0.5 border-l bg-gray-300"></span>
                                <div className="bg-gray-100 group-hover:bg-gray-200 size-6 overflow-hidden rounded-full duration-500">
                                    <div className="flex w-12 -translate-x-1/2 duration-500 ease-in-out group-hover:translate-x-0">
                                        <span className="flex size-6">
                                            <ArrowRight className="m-auto size-3 text-gray-600" />
                                        </span>
                                        <span className="flex size-6">
                                            <ArrowRight className="m-auto size-3 text-gray-600" />
                                        </span>
                                    </div>
                                </div>
                            </Link>
                        </AnimatedGroup>

                        <TextEffect
                            preset="fade-in-blur"
                            speedSegment={0.3}
                            as="h1"
                            className="mx-auto mt-8 max-w-4xl text-balance text-5xl max-md:font-semibold md:text-7xl lg:mt-16 xl:text-[5.25rem] text-white drop-shadow-2xl">
                            Reliable Car Service You Can Trust
                        </TextEffect>

                        <TextEffect
                            per="line"
                            preset="fade-in-blur"
                            speedSegment={0.3}
                            delay={0.5}
                            as="p"
                            className="mx-auto mt-8 max-w-2xl text-balance text-lg text-white/95 drop-shadow-lg">
                            Professional maintenance, diagnostics, and repairs — done quickly, transparently, and with care so you can get back on the road confidently.
                        </TextEffect>

                        <AnimatedGroup
                            variants={{
                                container: {
                                    visible: {
                                        transition: {
                                            staggerChildren: 0.05,
                                            delayChildren: 0.75,
                                        },
                                    },
                                },
                                ...transitionVariants,
                            }}
                            className="mt-12 flex flex-col items-center justify-center gap-4 md:flex-row">
                            <Button
                                asChild
                                size="lg"
                                className="rounded-xl px-8 text-base bg-white text-black hover:bg-white/90 border-0 shadow-2xl">
                                <Link href="#services">
                                    <span className="text-nowrap">View Services</span>
                                </Link>
                            </Button>
                            <Button
                                asChild
                                size="lg"
                                variant="outline"
                                className="rounded-xl px-8 text-base text-white border-white hover:bg-white/20 shadow-2xl">
                                <Link href="#pricing">
                                    <span className="text-nowrap">See Pricing</span>
                                </Link>
                            </Button>
                        </AnimatedGroup>
                    </div>
                </div>
            </section>
        </>
    )
}