/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#009BF4",
        danger: "#EC4D4D",
        warning: "#FEA000",
        success: "#689F38",
        "text-primary": "#212B36",
        "text-secondary": "#75828F",
      },
    },
  },
  plugins: [],
};
