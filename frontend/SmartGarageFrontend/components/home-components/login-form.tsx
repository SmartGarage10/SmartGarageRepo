"use client"

import {useState} from "react"
import {useRouter} from "next/navigation"
import {cn} from "@/lib/utils"
import {Button} from "@/components/ui/button"
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"

export function LoginForm({
                              className,
                              ...props
                          }: React.ComponentProps<"div">) {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [error, setError] = useState("")
    const [emailError, setEmailError] = useState(false)
    const [passwordError, setPasswordError] = useState(false)

    const router = useRouter()

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault()
        setError("")
        setEmailError(false)
        setPasswordError(false)

        try {
            // When making requests that require authentication with cookies (such as JSESSIONID),
            // we need to include the `credentials: "include"` option. This tells the browser to
            // send existing cookies along with the request and accept any cookies sent by the server.
            // Without this, the session cookie won't be saved, and subsequent requests will be unauthenticated.
            const response = await fetch("http://localhost:8080/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                credentials: "include", // it is import for Cookie
                body: JSON.stringify({email, password}),
            })

            if (!response.ok) {
                const errorText = await response.text()

                // Set global error message and mark both fields invalid
                setError(errorText || "Invalid credentials")
                setEmailError(true)
                setPasswordError(true)
                return
            }

            const data = await response.json()
            console.log("Login success:", data)

            router.push("/dashboard")
        } catch (err) {
            setError("Network error or server unavailable.")
        }
    }

    return (
        <div className={cn("flex flex-col gap-6", className)} {...props}>
            <Card>
                <CardHeader>
                    <CardTitle>Login to your account</CardTitle>
                    <CardDescription>
                        Enter your email below to login to your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit}>
                        <div className="flex flex-col gap-6">
                            {/* Email Field */}
                            <div className="grid gap-3">
                                <Label htmlFor="email">Email</Label>
                                <Input
                                    id="email"
                                    type="email"
                                    placeholder="m@example.com"
                                    value={email}
                                    onChange={(e) => {
                                        setEmail(e.target.value)
                                        setEmailError(false) // Reset error on input
                                    }}
                                    required
                                    className={cn(emailError && "border-red-500 focus-visible:ring-red-500")}
                                />
                            </div>

                            {/* Password Field */}
                            <div className="grid gap-3">
                                <div className="flex items-center">
                                    <Label htmlFor="password">Password</Label>
                                    <a
                                        href="#"
                                        className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
                                    >
                                        Forgot your password?
                                    </a>
                                </div>
                                <Input
                                    id="password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => {
                                        setPassword(e.target.value)
                                        setPasswordError(false) // Reset error on input
                                    }}
                                    required
                                    className={cn(passwordError && "border-red-500 focus-visible:ring-red-500")}
                                />
                            </div>

                            {/* Error Message */}
                            {error && <p className="text-sm text-red-500">{error}</p>}

                            {/* Buttons */}
                            <div className="flex flex-col gap-3">
                                <Button type="submit" className="w-full">
                                    Login
                                </Button>
                                <Button variant="outline" className="w-full">
                                    Login with Google
                                </Button>
                            </div>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
