import React from 'react'
import Image from 'next/image'

interface LogoProps {
    className?: string
    width?: number
    height?: number
}

export function Logo({ className = "", width = 80, height = 40 }: LogoProps) {
    return (
        <div className={`relative ${className}`}>
            <Image
                src="/logo.png"
                alt="Apex Auto Logo"
                width={width}
                height={height}
                className="object-contain"
                priority
            />
        </div>
    )
}