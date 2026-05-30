import type { AppProps } from "next/app";
import Head from "next/head";
import "../styles/globals.css";

export default function App({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>손맛 Son-Mat · Korean Recipe Discovery</title>
        <meta
          name="description"
          content="Discover Korean recipes from the ingredients you already have."
        />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Ctext y='.9em' font-size='90'%3E🍚%3C/text%3E%3C/svg%3E" />
      </Head>
      <Component {...pageProps} />
    </>
  );
}
