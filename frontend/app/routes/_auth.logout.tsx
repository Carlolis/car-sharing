import { redirect, ActionFunctionArgs } from "@remix-run/node";

export async function action({ request }: ActionFunctionArgs) {
  return redirect("/login", {
    headers: {
      "Set-Cookie": "token=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT",
    },
  });
}

export async function loader() {
  return redirect("/login");
}