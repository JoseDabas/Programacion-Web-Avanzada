/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: "#FFFFFF",
                secondary: "#B89E78", // Dorado/Tostado para detalles y botones
            }
        },
    },
    plugins: [

    ],
}