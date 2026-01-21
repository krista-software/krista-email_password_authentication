import { useTranslation } from "react-i18next";

export function TermsAndConditions() {
  const { t } = useTranslation();

  return (
    <div className="mt-3 flex justify-center items-center">
      <a href="#" className="text-xs text-text-secondary underline">
        {t("PRIVACY_POLICY")}
      </a>
      <span className="text-text-secondary block px-1">•</span>
      <a href="#" className="text-xs text-text-secondary underline">
        {t("TERMS_AND_CONDITIONS")}
      </a>
    </div>
  );
}
