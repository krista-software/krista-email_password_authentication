import { useTranslation } from "react-i18next";

export type Conditions = {
  minimumChar: boolean;
  upperCase: boolean;
  lowerCase: boolean;
  minimumNumber: boolean;
  minimumSpecialChar: boolean;
};

export function PasswordRequirements({ conditions }: { conditions: Conditions }) {
  const { t } = useTranslation();
  const successClass = "text-xs text-success mt-1";
  const errorClass = "text-xs text-danger mt-1";
  return (
    <>
      <p className="text-xs text-text-secondary">a-z, A-Z, 0-9, @, $, !, %, *, #, ?, & allowed</p>
      <p className={conditions.minimumChar ? successClass : errorClass}>{t("MINIMUM_CHARACTERS")}</p>
      <p className={conditions.upperCase ? successClass : errorClass}>{t("MINIMUM_UPPERCASE_LETTERS")}</p>
      <p className={conditions.lowerCase ? successClass : errorClass}>{t("MINIMUM_LOWERCASE_LETTERS")}</p>
      <p className={conditions.minimumNumber ? successClass : errorClass}>{t("MINIMUM_NUMBERS")}</p>
      <p className={conditions.minimumSpecialChar ? successClass : errorClass}>{t("MINIMUM_SPECIAL_CHARACTERS")}</p>
    </>
  );
}
