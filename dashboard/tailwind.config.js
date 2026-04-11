export default {
    content: ["./index.html", "./src/**/*.{ts,tsx}"],
    theme: {
        extend: {
            colors: {
                brand: {
                    teal: "#0D9488",
                    sand: "#F4E6D0",
                    clay: "#B45309",
                    slate: "#1F2937"
                }
            },
            boxShadow: {
                card: "0 8px 24px rgba(15, 23, 42, 0.08)"
            },
            keyframes: {
                pulseRed: {
                    "0%, 100%": { transform: "scale(1)", opacity: "1" },
                    "50%": { transform: "scale(1.3)", opacity: "0.4" }
                }
            },
            animation: {
                pulseRed: "pulseRed 1.2s ease-in-out infinite"
            }
        }
    },
    plugins: []
};
