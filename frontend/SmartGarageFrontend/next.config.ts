import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    // 1. Add the Images configuration here
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'images.pexels.com',
                port: '',
                pathname: '/**',
            },
            // Add any other image domains you might use
            {
                protocol: 'https',
                hostname: 'images.unsplash.com',
                port: '',
                pathname: '/**',
            },
        ],
    },

    // Proxy API requests to your Spring backend
    async rewrites() {
        return [
            {
                source: '/api/:path*',
                destination: 'http://localhost:8080/:path*',
            },
            // Add fallback for missing static files
            {
                source: '/_next/static/:path*',
                destination: '/static/:path*',
            },
        ];
    },

    // CORS headers configuration
    async headers() {
        return [
            {
                source: '/api/:path*',
                headers: [
                    { key: 'Access-Control-Allow-Credentials', value: 'true' },
                    { key: 'Access-Control-Allow-Origin', value: 'http://localhost:3000' }, // Be specific for security
                    { key: 'Access-Control-Allow-Methods', value: 'GET,OPTIONS,PATCH,DELETE,POST,PUT' },
                    {
                        key: 'Access-Control-Allow-Headers',
                        value: 'X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version, Authorization'
                    },
                ],
            },
        ];
    },

    // Development optimizations
    poweredByHeader: false,
    reactStrictMode: true,
    productionBrowserSourceMaps: false, // Disable source maps in development

    // Handle TypeScript errors during development
    typescript: {
        ignoreBuildErrors: true,
    },

    // Webpack configuration to ignore missing modules
    webpack: (config) => {
        config.resolve.fallback = {
            ...config.resolve.fallback,
            fs: false,
            net: false,
            tls: false,
        };
        return config;
    },
};

export default nextConfig;