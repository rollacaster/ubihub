(ns shopping-list.actions)

(def init
  [{:type :add-good, :uuid "5683557f-cf7b-4bb3-a58a-f9b54d511be6", :name "Banana" :category "8442ab6f-69df-49ef-a1dd-a2125197e063"}
   {:type :add-good, :uuid "5d63e80a-914a-49f7-8200-394dc69d8b28", :name "Apfel" :category "8442ab6f-69df-49ef-a1dd-a2125197e063"}
   {:type :add-good, :uuid "86b1732d-4573-4ac5-b2d9-0d874e7cf586", :name "Salat" :category "8442ab6f-69df-49ef-a1dd-a2125197e063"}
   {:type :add-good, :uuid "7cdd6804-9f0c-4c45-bced-2ae4519ac17f", :name "Trauben" :category "8442ab6f-69df-49ef-a1dd-a2125197e063"}
   {:type :add-good, :uuid "d39f93d8-93a6-4066-9164-1a8ee91c44de", :name "Gurke" :category "b1a88bd2-6011-444a-809f-079c86cea3a4"}
   {:type :add-good, :uuid "755572e3-29de-480f-b6aa-ad4c9c7dd657", :name "Kartoffeln" :category "b1a88bd2-6011-444a-809f-079c86cea3a4"}
   {:type :add-good, :uuid "1602d27d-b2ff-47e2-acb8-e1a935b3866b", :name "Karotten" :category "b1a88bd2-6011-444a-809f-079c86cea3a4"}
   {:type :add-good, :uuid "c39f731b-76d0-4fa9-a5ea-e230310dd0e3", :name "Zwiebeln" :category "b1a88bd2-6011-444a-809f-079c86cea3a4"}
   {:type :add-good, :uuid "c8635b48-c815-4e1e-a142-b5c45759aa9e", :name "Knoblauch" :category "b1a88bd2-6011-444a-809f-079c86cea3a4"}
   {:type :add-good, :uuid "09d3c147-13d2-46f3-82a1-a21928c22755", :name "Eis" :category "fa97219a-cb69-4e4f-ae34-b60b08bf324e"}
   {:type :add-good, :uuid "557d8a55-bdc1-4057-9cd0-b7bb69d5cf47", :name "Pizza" :category "fa97219a-cb69-4e4f-ae34-b60b08bf324e"}
   {:type :add-good, :uuid "f75e1ca7-6939-4912-aca7-12aa3a718184", :name "Semmeln" :category "fa97219a-cb69-4e4f-ae34-b60b08bf324e"}
   {:type :add-good, :uuid "d2625f82-1f73-41b5-a0e0-591cae8c015f", :name "Brezeln" :category "fa97219a-cb69-4e4f-ae34-b60b08bf324e"}
   {:type :add-good, :uuid "34f88891-af04-4064-8ced-dcedfcbb40c1", :name "Blumenkohl" :category "fa97219a-cb69-4e4f-ae34-b60b08bf324e"}
   {:type :add-good, :uuid "b40983ad-14aa-4f5c-aae2-8358b2e7bb59", :name "Brokoli" :category "fa97219a-cb69-4e4f-ae34-b60b08bf324e"}
   {:type :add-good, :uuid "7325e4c4-d541-4595-9a96-a34307d1f9f1", :name "Spinat" :category "fa97219a-cb69-4e4f-ae34-b60b08bf324e"}
   {:type :add-good, :uuid "88f0f73a-35b3-4fbe-9460-cbaf31972e3f", :name "Pfeffer" :category "d6d5599f-9f5e-4eed-98b3-b80f2a677a62"}
   {:type :add-good, :uuid "ab44a98f-c2fe-42e9-b5de-36b17cb7ab21", :name "Salz" :category "d6d5599f-9f5e-4eed-98b3-b80f2a677a62"}
   {:type :add-good, :uuid "3b6735be-adbe-4f37-ae20-3501f1ec4b9a", :name "Kurkuma" :category "d6d5599f-9f5e-4eed-98b3-b80f2a677a62"}
   {:type :add-good, :uuid "378eaf6c-21f5-4cba-b29d-f87f4cedeaae", :name "Paprika" :category "d6d5599f-9f5e-4eed-98b3-b80f2a677a62"}
   {:type :add-good, :uuid "de66ab17-1b09-46fd-97b5-2d528d998a2b", :name "Curry" :category "d6d5599f-9f5e-4eed-98b3-b80f2a677a62"}
   {:type :add-good, :uuid "b54173de-973c-4fb0-8326-ab3fc29f7120", :name "Rosmarin" :category "d6d5599f-9f5e-4eed-98b3-b80f2a677a62"}
   {:type :add-good, :uuid "6ad9900c-2227-4b89-9667-06a50e173f84", :name "Kellogs" :category "6d8eefc1-2258-4f88-8f04-a695379b1b70"}
   {:type :add-good, :uuid "dea8dcb0-23c4-44cc-9bb2-e693d286c640", :name "Tortellini" :category "1d5fc037-00f8-4cef-986f-10d56afbfae4"}
   {:type :add-good, :uuid "458985d5-5085-4c71-a578-061a0d30c291", :name "Nudeln" :category "1d5fc037-00f8-4cef-986f-10d56afbfae4h"}
   {:type :add-good, :uuid "d6a069c6-a79b-4fb0-b6aa-770fe43abc44", :name "Spaghetti" :category "1d5fc037-00f8-4cef-986f-10d56afbfae4"}
   {:type :add-good, :uuid "7d43c4a9-1e5b-4837-b87a-1d4faa2947e0", :name "Reis" :category "1d5fc037-00f8-4cef-986f-10d56afbfae4"}
   {:type :add-good, :uuid "36513509-4c28-499a-b259-dcbd2fd9a97d", :name "Couscous" :category "1d5fc037-00f8-4cef-986f-10d56afbfae4"}
   {:type :add-good, :uuid "ca01c290-4d5a-4216-bd5e-2e131cee85a1", :name "Spätzle" :category "1d5fc037-00f8-4cef-986f-10d56afbfae4"}
   {:type :add-good, :uuid "cfa029aa-0bb2-455a-8304-a369c21f9e6d", :name "Brot" :category "b56a2e00-5ef4-4362-9faa-068423a4dba0"}
   {:type :add-good, :uuid "147c2344-820b-4bb6-8ba5-88a515cb9a19", :name "Baguette" :category "b56a2e00-5ef4-4362-9faa-068423a4dba0"}
   {:type :add-good, :uuid "1e0b84eb-fba3-476a-b231-f18583c09413", :name "Toast" :category "b56a2e00-5ef4-4362-9faa-068423a4dba0"}
   {:type :add-good, :uuid "b9674df8-6b80-4e5a-aa5b-a3cfffb4417f", :name "Wraps" :category "b56a2e00-5ef4-4362-9faa-068423a4dba0"}
   {:type :add-good, :uuid "a3e10cba-0caa-44aa-ae1d-8916c33983ee", :name "Buns" :category "b56a2e00-5ef4-4362-9faa-068423a4dba0"}
   {:type :add-good, :uuid "a6598afe-56b0-4e95-93d4-609bb2269e23", :name "Hotdog-Brot" :category "b56a2e00-5ef4-4362-9faa-068423a4dba0"}
   {:type :add-good, :uuid "f4da1f51-c51c-4ed4-ae40-0ff826916685", :name "Fleisch (Pute)" :category "5f8fa8a3-8d27-4ce5-a62b-7b49a3c4f519"}
   {:type :add-good, :uuid "07b0ad60-6993-4dab-bb6f-3472edf77d70", :name "Hackfleisch" :category "5f8fa8a3-8d27-4ce5-a62b-7b49a3c4f519"}
   {:type :add-good, :uuid "844674a6-1ce1-4aec-9e2f-cce51dcab53a", :name "Schnitzel" :category "5f8fa8a3-8d27-4ce5-a62b-7b49a3c4f519"}
   {:type :add-good, :uuid "9abd8ba1-9f27-4f09-bba6-3a5016a900c0", :name "Leberkas" :category "23d98ff5-7753-45c5-887c-9a1bb9a86e39"}
   {:type :add-good, :uuid "b4d74dab-05c4-4503-9d20-405f3a21cb1a", :name "Schinkenwürfel" :category "23d98ff5-7753-45c5-887c-9a1bb9a86e39"}
   {:type :add-good, :uuid "5b54b00b-6002-4bed-b3a5-a526799ab108", :name "Schinken" :category "23d98ff5-7753-45c5-887c-9a1bb9a86e39"}
   {:type :add-good, :uuid "6692dd3a-455e-4415-bd81-28e50d9a16ca", :name "Würste" :category "23d98ff5-7753-45c5-887c-9a1bb9a86e39"}
   {:type :add-good, :uuid "157ec274-83be-4003-8ee6-875523ef001d", :name "Cabanossi" :category "23d98ff5-7753-45c5-887c-9a1bb9a86e39"}
   {:type :add-good, :uuid "f965ecb8-057c-4d66-8b33-16c65d289cde", :name "Mais" :category "c20a2fce-9c48-4745-9174-daba63285d5b"}
   {:type :add-good, :uuid "3b5fa632-691c-4a13-aff9-d0a4c8726abc", :name "Bohnen" :category "c20a2fce-9c48-4745-9174-daba63285d5b"}
   {:type :add-good, :uuid "9c25d8c8-afbb-496f-ad14-14ea21274e80", :name "Erbsen" :category "c20a2fce-9c48-4745-9174-daba63285d5b"}
   {:type :add-good, :uuid "c133f243-08e0-485f-a8eb-8d28fc50013f", :name "Mehl" :category "5c5e49d1-a8fc-4db0-8935-0ef431889359"}
   {:type :add-good, :uuid "9e334c60-cb4c-4fa8-98ac-01157f6857a2", :name "Zucker" :category "5c5e49d1-a8fc-4db0-8935-0ef431889359"}
   {:type :add-good, :uuid "88a6092f-5fd1-43fb-87d8-460fd962c4c4", :name "Milch" :category "ef1abf54-231d-4fb2-a31c-3643617dedbd"}
   {:type :add-good, :uuid "1ae9484a-8922-4115-bc94-0bb1697e9316", :name "Schmand" :category "ef1abf54-231d-4fb2-a31c-3643617dedbd"}
   {:type :add-good, :uuid "eeb0ff57-aabb-483f-86c1-68f97aa318c5", :name "Creme Fraiche" :category "ef1abf54-231d-4fb2-a31c-3643617dedbd"}
   {:type :add-good, :uuid "c93854bb-63e7-44d0-948d-5558795b5aa4", :name "Sahne" :category "ef1abf54-231d-4fb2-a31c-3643617dedbd"}
   {:type :add-good, :uuid "c29ede0a-e1e3-4afd-9228-0caf3a83db0d", :name "Käse" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "acfcedd9-8457-4a65-bfdf-fca807f52b1f", :name "Geriebener Käse" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "1729657f-345c-4eeb-9d83-b4e96335a178", :name "Cheddarkäse" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "b9879d10-c5af-4f94-a973-8b9bbeae9802", :name "Frischkäse" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "099e6bb5-edb7-4785-b3c2-80efae853621", :name "Frischkäse (Kräuter)" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "ec82e0e8-e8a1-42ea-9160-c747b2c24774", :name "Camenbert" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "8c12989d-de3c-4f98-90d6-324b59cd8cfc", :name "Feta" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "f31a5dda-bc86-4a5c-bb71-32ec2a8092ab", :name "Runder Käse" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "53c1a272-e4d7-4ad0-b522-a5d03388ce28", :name "Hefe" :category "909db043-2549-4eee-a950-41bcfc489890"}
   {:type :add-good, :uuid "57477805-7f7e-481d-ad25-62e64ca36019", :name "Schokobons" :category "24fbd7e5-fcdc-4753-b487-8255226b82f3"}
   {:type :add-good, :uuid "d59e0da1-5df8-4168-821f-19bc40639022", :name "Chips" :category "24fbd7e5-fcdc-4753-b487-8255226b82f3"}
   {:type :add-good, :uuid "5dbd5909-2191-460a-b28a-1b8650bdaae0", :name "Toffifee" :category "24fbd7e5-fcdc-4753-b487-8255226b82f3"}
   {:type :add-good, :uuid "9a57494c-17f1-44b5-b0a6-0d64421c6758", :name "Knoppers" :category "24fbd7e5-fcdc-4753-b487-8255226b82f3"}
   {:type :add-good, :uuid "3ac152fd-f2b0-4f5f-912f-74784a63c5d2", :name "Kosmetiktücher" :category "f3f8c266-1c53-4de6-9082-17f4c10b8f14"}
   {:type :add-good, :uuid "50ff065c-acf8-4f69-a2b1-0877845f8056", :name "Zahnpasta" :category "f3f8c266-1c53-4de6-9082-17f4c10b8f14"}
   {:type :add-good, :uuid "982ab01f-1a46-415e-91cf-9b825c9e73a9", :name "Eier" :category "505f1cd0-d40f-46ac-867c-97cf8e003962"}
   {:type :add-category :uuid "8442ab6f-69df-49ef-a1dd-a2125197e063" :name "Obst"}
   {:type :add-category :uuid "b1a88bd2-6011-444a-809f-079c86cea3a4" :name "Gemüse"}
   {:type :add-category :uuid "fa97219a-cb69-4e4f-ae34-b60b08bf324e" :name "Tiefkühl"}
   {:type :add-category :uuid "d6d5599f-9f5e-4eed-98b3-b80f2a677a62" :name "Gewürze"}
   {:type :add-category :uuid "b56a2e00-5ef4-4362-9faa-068423a4dba0" :name "Brot & Co"}
   {:type :add-category :uuid "6d8eefc1-2258-4f88-8f04-a695379b1b70" :name "Müsli"}
   {:type :add-category :uuid "1d5fc037-00f8-4cef-986f-10d56afbfae4" :name "Beilage"}
   {:type :add-category :uuid "5f8fa8a3-8d27-4ce5-a62b-7b49a3c4f519" :name "Fleisch"}
   {:type :add-category :uuid "23d98ff5-7753-45c5-887c-9a1bb9a86e39" :name "Wursttheke"}
   {:type :add-category :uuid "c20a2fce-9c48-4745-9174-daba63285d5b" :name "Dosen"}
   {:type :add-category :uuid "5c5e49d1-a8fc-4db0-8935-0ef431889359" :name "Backen"}
   {:type :add-category :uuid "ef1abf54-231d-4fb2-a31c-3643617dedbd" :name "Milch & Co"}
   {:type :add-category :uuid "909db043-2549-4eee-a950-41bcfc489890" :name "Käse & Co"}
   {:type :add-category :uuid "f3f8c266-1c53-4de6-9082-17f4c10b8f14" :name "Bad"}
   {:type :add-category :uuid "24fbd7e5-fcdc-4753-b487-8255226b82f3" :name "Süßigkeiten"}
   {:type :add-category :uuid "505f1cd0-d40f-46ac-867c-97cf8e003962" :name "Eier"}])
