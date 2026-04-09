// components/home-components/logo.tsx
import React from 'react'

interface LogoProps {
    className?: string
    width?: number
    height?: number
}

export function Logo({ className = "", width = 170, height = 55 }: LogoProps) {
    return (
        <div className={`relative flex items-center gap-1 ${className}`}>
            {/* Fox Logo */}
            <div className="relative flex-shrink-0">
                <div className="absolute inset-0 bg-lime-500/40 blur-xl rounded-full animate-pulse"></div>

                {/* NEW FOX SVG */}
                <svg
                    width="65"
                    height="65"
                    viewBox="0 0 300 300"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    className="relative drop-shadow-lg"
                    style={{ filter: "drop-shadow(0 0 8px rgba(163,230,53,0.5))" }}
                >
                    <defs>
                        <linearGradient id="foxBody" x1="0%" y1="0%" x2="100%" y2="100%">
                            <stop offset="0%" stopColor="#a3e635" />
                            <stop offset="50%" stopColor="#84cc16" />
                            <stop offset="100%" stopColor="#4d7c0f" />
                        </linearGradient>

                        <linearGradient id="foxFlame" x1="0%" y1="0%" x2="100%" y2="100%">
                            <stop offset="0%" stopColor="#d9f99d" />
                            <stop offset="100%" stopColor="#84cc16" />
                        </linearGradient>
                    </defs>

                    {/* Flaming Tail */}
                    <path
                        d="
                            M 60 200
                            C 40 160, 50 110, 100 80
                            C 150 50, 210 70, 230 110
                            C 200 105, 170 120, 150 150
                            C 130 180, 140 210, 170 235
                            C 140 240, 100 230, 75 210
                            C 65 205, 62 202, 60 200
                        "
                        fill="url(#foxFlame)"
                    />

                    {/* Body */}
                    <path
                        d="
                            M 140 140
                            C 160 120, 190 110, 220 120
                            C 200 130, 185 150, 180 170
                            C 175 190, 180 205, 195 220
                            C 160 215, 140 185, 140 140
                        "
                        fill="url(#foxBody)"
                    />

                    {/* Head looking back */}
                    <path
                        d="
                            M 200 120
                            C 185 100, 160 95, 140 110
                            C 150 110, 160 120, 165 130
                            C 170 140, 175 145, 185 150
                            C 188 140, 192 130, 200 120
                        "
                        fill="url(#foxBody)"
                    />

                    {/* Ear */}
                    <path
                        d="
                            M 165 115
                            L 155 100
                            L 145 110
                            Z
                        "
                        fill="#d9f99d"
                        opacity="0.8"
                    />

                    {/* Eye */}
                    <circle cx="170" cy="130" r="3" fill="#0a0a0a" />
                </svg>
            </div>

            {/* Text */}
            <div className="flex flex-col justify-center leading-tight">
                <span
                    className="text-2xl font-black text-white tracking-wider"
                    style={{ letterSpacing: "0.1em" }}
                >
                    RIVAL
                </span>
                <span className="text-[10px] text-lime-400 font-bold tracking-[0.3em] uppercase">
                    AUTOMOTIVE
                </span>
            </div>
        </div>
    )
}
