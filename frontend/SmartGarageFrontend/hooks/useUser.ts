"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { User } from "@/types/user"

export function useUser() {
    const [user, setUser] = useState<User | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null) // ✅ define state
    const router = useRouter()

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await fetch("http://localhost:8080/auth/user", {
                    method: "GET",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })

                if (!response.ok) {
                    if (response.status === 401) {
                        router.push("/login")
                        return
                    }
                    throw new Error(`HTTP error! status: ${response.status}`)
                }

                const userData = await response.json()
                setUser(userData)

            } catch (err) {
                console.error("Failed to fetch user:", err)
                setError("Failed to load user data")
                router.push("/login")
            } finally {
                setLoading(false)
            }
        }

        fetchUser()
    }, [router])

    return { user, loading, error, setUser }
}
