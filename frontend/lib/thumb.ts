// Deterministic, dependency-free thumbnails: a warm gradient plus a food emoji
// keyed off the recipe name. Keeps the app attractive offline without bundling
// or hot-linking images.

const EMOJI: Record<string, string> = {
  Bibimbap: "🍚",
  Japchae: "🍜",
  "Kimchi Jjigae": "🍲",
  Tteokbokki: "🌶️",
  Bulgogi: "🥩",
  "Doenjang Jjigae": "🥘",
  "Sundubu Jjigae": "🍲",
  "Haemul Pajeon": "🥞",
  "Gyeran Jjim": "🍳",
  "Miyeok Guk": "🥬",
  Kongguksu: "🍜",
  "Sigeumchi Namul": "🥬",
  "Gamja Jorim": "🥔",
  "Kimchi Bokkeumbap": "🍚",
  Samgyetang: "🍗",
};

const GRADIENTS = [
  "linear-gradient(135deg, #e8a87c 0%, #cc785c 100%)",
  "linear-gradient(135deg, #d4a373 0%, #b5603f 100%)",
  "linear-gradient(135deg, #a3b18a 0%, #6a7d5b 100%)",
  "linear-gradient(135deg, #e9c46a 0%, #cc785c 100%)",
  "linear-gradient(135deg, #cb997e 0%, #8a5a44 100%)",
  "linear-gradient(135deg, #b08968 0%, #7f5539 100%)",
];

export function thumbEmoji(name: string): string {
  return EMOJI[name] ?? "🍱";
}

export function thumbGradient(name: string): string {
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = (hash * 31 + name.charCodeAt(i)) >>> 0;
  }
  return GRADIENTS[hash % GRADIENTS.length];
}
