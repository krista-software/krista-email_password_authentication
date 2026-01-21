import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { i18n_de } from "./de";
import { i18n_en } from "./en";
import { i18n_es } from "./es";
import { i18n_fr } from "./fr";
import { i18n_hi } from "./hi";
import { i18n_mr } from "./mr";

const resources = {
  en: {
    translation: i18n_en,
  },
  de: {
    translation: i18n_de,
  },
  es: {
    translation: i18n_es,
  },
  fr: {
    translation: i18n_fr,
  },
  hi: {
    translation: i18n_hi,
  },
  mr: {
    translation: i18n_mr,
  },
};

i18n
  .use(initReactI18next)
  .use(LanguageDetector)
  .init({
    resources,
    debug: true,
    fallbackLng: "en",
    interpolation: {
      escapeValue: false,
    },
    detection: {
      order: ["localStorage", "querystring", "navigator", "subdomain"],
    },
  });

export default i18n;
