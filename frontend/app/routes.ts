import { route, type RouteConfig } from "@react-router/dev/routes";


// export default flatRoutes() satisfies RouteConfig;
export default [
  route("/", "./routes/index.tsx"),
  route("/dashboard", "./routes/dashboard.tsx"),
  route("/login", "./routes/login.tsx"),
  // pattern ^           ^ module file
] satisfies RouteConfig;
