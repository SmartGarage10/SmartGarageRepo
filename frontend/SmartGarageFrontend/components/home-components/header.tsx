'use client'

import Link from 'next/link'
import { Logo } from '@/components/home-components/logo'
import { Menu, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import React from 'react'
import { cn } from '@/lib/utils'

const defaultNavbarProps = {
    menu: [
        { title: "Home", url: "/" },
        { title: "About Us", url: "#about" },
        { title: "How We Work", url: "#how-we-work" },
        { title: "Services", url: "#services" },
        // { title: "Pricing", url: "#pricing" },
        { title: "FAQ", url: "#faq" },
        { title: "Contact Us", url: "#contact" }
    ],
    auth: {
        login: { title: "Login", url: "/login" },
    }
};

const menuItems = defaultNavbarProps.menu.map(item => ({
    name: item.title,
    href: item.url
}));

export const HeroHeader = () => {
    const [menuState, setMenuState] = React.useState(false)
    const [isScrolled, setIsScrolled] = React.useState(false)

    React.useEffect(() => {
        const handleScroll = () => setIsScrolled(window.scrollY > 50)
        window.addEventListener('scroll', handleScroll)
        return () => window.removeEventListener('scroll', handleScroll)
    }, [])

    return (
        <header className="relative z-30 w-full">
            <nav className="fixed top-0 left-0 z-20 w-full px-2">

                {/* WRAPPER */}
                <div
                    className={cn(
                        "mx-auto transition-all duration-300",

                        // MOBILE — always same size
                        "max-w-full px-6 py-4 mt-3",

                        // DESKTOP — animated shrink
                        isScrolled
                            ? "lg:mt-5 lg:max-w-7xl lg:px-8 lg:py-4 bg-black/70 border border-lime-500/20 rounded-2xl backdrop-blur-xl shadow-xl shadow-black/40"
                            : "lg:mt-3 lg:max-w-9xl lg:px-8 lg:py-6 border border-transparent bg-transparent"
                    )}
                >

                    <div className="relative flex flex-wrap items-center justify-between gap-10 ">

                        {/* LOGO + MOBILE TOGGLE */}
                        <div className="flex w-full justify-between lg:w-auto ">
                            <Link href="/" aria-label="home" className="flex items-center gap-3">
                                <Logo />
                            </Link>

                            <button
                                onClick={() => setMenuState(!menuState)}
                                aria-label={menuState ? 'Close Menu' : 'Open Menu'}
                                className="relative z-20 -m-2.5 -mr-4 block cursor-pointer p-2.5 text-white lg:hidden"
                            >
                                <Menu className="in-data-[state=active]:opacity-0 in-data-[state=active]:scale-0 size-6 duration-200" />
                                <X className="absolute inset-0 m-auto size-6 opacity-0 scale-0 in-data-[state=active]:opacity-100 in-data-[state=active]:scale-100 duration-200" />
                            </button>
                        </div>

                        {/* DESKTOP MENU */}
                        <div className="hidden lg:flex flex-1 justify-center">
                            <ul className="
        flex
        gap-4 md:gap-4 xl:gap-7
        text-sm md:text-base xl:text-md
        font-medium
    ">
                                {menuItems.map((item, index) => (
                                    <li key={index}>
                                        <Link
                                            href={item.href}
                                            className="
                        text-gray-300
                        hover:text-lime-400
                        transition-colors
                        duration-150
                        px-1 md:px-2
                    "
                                        >
                                            {item.name}
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        </div>


                        {/* DESKTOP LOGIN BUTTON */}
                        <div className="hidden lg:flex items-center px-4 gap-4">
                            <Button
                                asChild
                                size="sm"
                                className="bg-lime-500 text-black hover:bg-lime-400 font-semibold shadow-lg shadow-lime-500/30 hover:shadow-xl hover:shadow-lime-500/40 transition-all duration-300 h-11 px-6"
                            >
                                <Link href={defaultNavbarProps.auth.login.url}>
                                    {defaultNavbarProps.auth.login.title}
                                </Link>
                            </Button>
                        </div>

                        {/* MOBILE MENU */}
                        <div
                            className={cn(
                                "bg-black/90 backdrop-blur-xl in-data-[state=active]:block border border-lime-500/20 mb-6 hidden w-full rounded-2xl p-6 shadow-2xl shadow-black/40 lg:hidden",
                                menuState && "block"
                            )}
                        >
                            <ul className="space-y-6 text-base mb-6 pb-6 border-b border-lime-500/20">
                                {menuItems.map((item, index) => (
                                    <li key={index}>
                                        <Link
                                            href={item.href}
                                            className="text-gray-300 hover:text-lime-400 block transition-colors duration-150"
                                            onClick={() => setMenuState(false)}
                                        >
                                            {item.name}
                                        </Link>
                                    </li>
                                ))}
                            </ul>

                            <Button
                                asChild
                                size="lg"
                                className="w-full bg-lime-500 text-black hover:bg-lime-400 font-semibold shadow-lg shadow-lime-500/30 h-12"
                            >
                                <Link href={defaultNavbarProps.auth.login.url}>
                                    {defaultNavbarProps.auth.login.title}
                                </Link>
                            </Button>
                        </div>

                    </div>
                </div>
            </nav>
        </header>
    )
}
