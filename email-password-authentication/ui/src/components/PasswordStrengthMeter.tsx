import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

export function PasswordStengthMeter({ strength }: { strength: number }) {
  const { t } = useTranslation();
  const colors = ["#000000", "#EC4D4D", "#FEA000", "#CDDC39", "#73C34D"];

  const [barColors, setBarColors] = useState([{}, {}, {}, {}, {}]);
  const defaultColor = { backgroundColor: "#DDDDDD" };
  const strengthToText = ["WEAK", "NOT_GOOD", "AVERAGE", "GOOD", "STRONG"];

  useEffect(() => {
    const newBar = [];
    for (let i = 0; i < barColors.length; i++) {
      if (i <= strength) {
        newBar.push({ backgroundColor: colors[strength] });
      } else {
        newBar.push(defaultColor);
      }
    }
    setBarColors(newBar);
  }, [strength]);

  return (
    <>
      <p className="text-xs text-text-secondary">
        {t("PASSWORD_STRENGTH")}{" "}
        {strength >= 0 ? <span className="text-danger">{t(strengthToText[strength])}</span> : null}
      </p>
      <div className="flex justify-between gap-2 mt-2">
        <div style={barColors[0]} className="h-1 rounded-sm grow"></div>
        <div style={barColors[1]} className="h-1 rounded-sm grow"></div>
        <div style={barColors[2]} className="h-1 rounded-sm grow"></div>
        <div style={barColors[3]} className="h-1 rounded-sm grow"></div>
        <div style={barColors[4]} className="h-1 rounded-sm grow"></div>
      </div>
    </>
  );
}
