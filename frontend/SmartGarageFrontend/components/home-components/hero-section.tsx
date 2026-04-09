// app/page.tsx or HeroSection component
"use client"

import React from 'react'
import Link from 'next/link'
import { ArrowRight, Phone, Calendar, Clock } from 'lucide-react'
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

            {/* Hero Section - Full screen with proper padding for fixed header */}
            <section className="relative w-full min-h-screen flex items-center">

                {/* Background Image with Overlay */}
                <div className="absolute inset-0">
                    <Image
                        src="https://images.unsplash.com/photo-1530046339160-ce3e530c7d2f?q=80&w=2070&auto=format&fit=crop"
                        alt="Professional Auto Repair Service"
                        fill
                        className="object-cover object-center"
                        priority
                        sizes="100vw"
                    />
                    {/* Lighter Dark Overlay for Better Image Visibility */}
                    <div className="absolute inset-0 bg-gradient-to-r from-black/80 via-black/60 to-black/70"></div>
                    <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-transparent"></div>
                    {/* Reduced lime accent overlay */}
                    <div className="absolute inset-0 bg-lime-500/10 mix-blend-overlay"></div>
                </div>

                {/* Content Container */}
                <div className="relative z-10 w-full px-4 sm:px-6 lg:px-8 py-24 md:py-28 lg:py-32">
                    <div className="max-w-7xl mx-auto">
                        <div className="max-w-4xl">
                            {/* Main Headline */}
                            <AnimatedGroup variants={transitionVariants}>
                                <h1 className="mt-6 text-4xl sm:text-5xl md:text-6xl lg:text-7xl xl:text-8xl font-bold text-white leading-[1.2] drop-shadow-lg">
                                    Expert Auto Repair
                                </h1>
                            </AnimatedGroup>

                            {/* Subheadline */}
                            <AnimatedGroup variants={transitionVariants}>
                                <h2 className="block text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold leading-[1.2]">
                                    <span className="bg-gradient-to-r from-lime-400 via-lime-300 to-lime-500 bg-clip-text text-transparent drop-shadow-md">
                                        Precision Service You Can Trust
                                    </span>
                                </h2>
                            </AnimatedGroup>

                            {/* Badge */}
                            <AnimatedGroup variants={transitionVariants}>
                                <div className="mt-3 inline-flex items-center gap-3 rounded-full border border-lime-500/50 bg-black/40 backdrop-blur-md px-5 py-4 shadow-lg shadow-lime-500/20">
                                    <Clock className="size-4 text-lime-400" />
                                    <span className="text-lime-100 text-sm font-semibold tracking-wide">
                                        24/7 Emergency Service Available
                                    </span>
                                </div>
                            </AnimatedGroup>

                            {/* Description */}
                            <TextEffect
                                per="line"
                                preset="fade-in-blur"
                                speedSegment={0.3}
                                delay={0.4}
                                as="p"
                                className="mt-3 max-w-2xl text-base sm:text-lg md:text-xl text-gray-100 leading-relaxed drop-shadow"
                            >
                                Certified master technicians, state-of-the-art diagnostics, and a commitment to quality. From routine maintenance to complex repairs, we keep you on the road.
                            </TextEffect>

                            {/* CTA Buttons */}
                            <AnimatedGroup
                                variants={{
                                    container: {
                                        visible: {
                                            transition: {
                                                staggerChildren: 0.05,
                                                delayChildren: 0.6,
                                            },
                                        },
                                    },
                                    ...transitionVariants,
                                }}
                                className="mt-10 flex flex-col sm:flex-row gap-4"
                            >
                                <Button
                                    asChild
                                    size="lg"
                                    className="group relative rounded-full px-8 py-6 text-base font-semibold bg-lime-500 text-black hover:bg-lime-400 border-0 shadow-xl shadow-lime-500/50 transition-all duration-300 hover:scale-105 hover:shadow-2xl hover:shadow-lime-500/60"
                                >
                                    <Link href="#booking">
                                        <Calendar className="relative mr-2 size-5" />
                                        <span className="relative">Schedule Service</span>
                                        <ArrowRight className="relative ml-2 size-4 group-hover:translate-x-1 transition-transform duration-300" />
                                    </Link>
                                </Button>

                                <Button
                                    asChild
                                    size="lg"
                                    variant="outline"
                                    className="group relative rounded-full px-8 py-6 text-base font-semibold !text-white border-2 border-white/40 hover:border-lime-500 bg-black/30 hover:bg-lime-500/30 backdrop-blur-sm transition-all duration-300 hover:scale-105 shadow-lg"
                                >
                                    <Link href="tel:+1234567890">
                                        <Phone className="mr-2 size-4 group-hover:rotate-12 transition-transform duration-300" />
                                        <span className="!text-white drop-shadow">Emergency Call</span>
                                    </Link>
                                </Button>
                            </AnimatedGroup>
                        </div>
                    </div>
                </div>

                {/* Bottom Gradient - Reduced opacity */}
                <div className="absolute bottom-0 left-0 right-0 h-24 bg-gradient-to-t from-black/80 to-transparent pointer-events-none"></div>
            </section>
        </>
    )
}