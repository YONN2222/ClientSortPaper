<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/languages-dark.svg">
    <img src="../assets/icons/languages-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Languages</h1>

<p align="center">Ten languages included, every text editable.</p>

<p align="center"><a href="README.md">All docs</a></p>

These texts are used by `/sort` and `/clientsort`. The mod shows its own texts in the client language.

Set `language` in `config.yml` and run `/clientsort reload`.

| Code | Language |
|---|---|
| `en` | English |
| `zh` | Chinese |
| `es` | Spanish |
| `hi` | Hindi |
| `fr` | French |
| `ar` | Arabic |
| `pt` | Portuguese |
| `ru` | Russian |
| `de` | German |
| `ja` | Japanese |

The default Minecraft font cannot display Hindi and Arabic properly, use a resource pack with a suitable font if you need them.

## Your own texts

The files are copied to `plugins/ClientSortPaper/lang` on the first start. Edit one, or copy it and give it a new name such as `nl.yml`, then set `language: nl`. A missing key falls back to the bundled English text. Messages use [MiniMessage](https://docs.advntr.dev/minimessage/format.html), so colors and click events work.

## Placeholders

| Placeholder | Where | Meaning |
|---|---|---|
| `<prefix>` | every message | The text of the `prefix` key |
| `<label>` | status | The command label |
| `<count>`, `<total>` | status | Players with the mod, players online |
| `<input>` | unknown option | What the player typed |
| `<reason>` | sort failed | Why the server refused |
