import { useSearchParams } from "react-router-dom";
import { ShowMessage } from "../components/ShowMessage";
import { useContext, useEffect, useState } from "react";
import { DataContext } from "../context";

export function MessagePage() {
  const [searchParams] = useSearchParams();
  const [message, setMessage] = useState("");
  const [buttonText, setButtonText] = useState("");
  const context = useContext(DataContext);
  let redirectUrl: string | null = "";

  const onSubmit = () => {
    if (redirectUrl) {
      context.navigate(redirectUrl, false);
    }
  };

  useEffect(() => {
    redirectUrl = searchParams.get("redirectUrl");
    if (redirectUrl) {
      setButtonText("BACK");
    }
    setMessage(searchParams.get("message") || "");
  }, []);

  return <ShowMessage message={message} buttonText={buttonText} onSubmit={onSubmit}></ShowMessage>;
}
