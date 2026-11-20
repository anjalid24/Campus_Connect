package com.ljuniversity.companion.util;

import com.google.gson.*;
import com.ljuniversity.companion.model.PlannerTask;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/** Bounded, atomic local persistence. Corrupt data is never silently overwritten. */
public final class PlannerStore {
    private final Path file;
    private final List<PlannerTask> tasks = new ArrayList<>();
    private boolean writable = true;
    private String warning;
    private byte[] originalBytes;
    public PlannerStore(Path file) {
        this.file = file;
        try { load(); }
        catch (Exception e) { writable = false; warning = "Saved data could not be read. It has been left untouched. Export your current session before closing, and inspect the data file separately."; }
    }
    public static Path defaultPath() {
        return Path.of(System.getProperty("user.home"), ".campus-connect", "planner-v1.json");
    }
    private void load() throws IOException {
        if (!Files.exists(file)) return;
        if (Files.size(file) > 2_000_000) throw new IOException("Data file exceeds limit");
        JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        if (root.get("version").getAsInt() != 1) throw new IOException("Unsupported format");
        JsonArray rows = root.getAsJsonArray("tasks");
        if (rows.size() > 1000) throw new IOException("Too many tasks");
        List<PlannerTask> parsed = new ArrayList<>(); Set<String> ids = new HashSet<>();
        for (JsonElement element : rows) {
            JsonObject row = element.getAsJsonObject();
            PlannerTask task = new PlannerTask(row.get("id").getAsString(), row.get("title").getAsString(), row.get("notes").getAsString(), LocalDate.parse(row.get("due").getAsString()), row.get("category").getAsString(), row.get("done").getAsBoolean());
            if (!ids.add(task.id())) throw new IOException("Duplicate task ID");
            parsed.add(task);
        }
        tasks.addAll(parsed);
        originalBytes = Files.readAllBytes(file);
    }
    public synchronized List<PlannerTask> list(String query, String filter) {
        String search = query == null ? "" : query.strip().toLowerCase(Locale.ROOT);
        return tasks.stream().filter(t -> (t.title() + " " + t.notes()).toLowerCase(Locale.ROOT).contains(search))
            .filter(t -> "All tasks".equals(filter) || ("Completed".equals(filter) ? t.done() : !t.done()))
            .sorted(Comparator.comparing(PlannerTask::done).thenComparing(PlannerTask::due).thenComparing(PlannerTask::id)).toList();
    }
    public synchronized List<PlannerTask> all() { return List.copyOf(tasks); }
    public synchronized void put(PlannerTask task) throws IOException {
        List<PlannerTask> next = new ArrayList<>(tasks);
        next.removeIf(t -> t.id().equals(task.id()));
        if (next.size() >= 1000) throw new IllegalArgumentException("The planner supports up to 1000 tasks.");
        next.add(task); commit(next);
    }
    public synchronized void remove(String id) throws IOException {
        List<PlannerTask> next = new ArrayList<>(tasks);next.removeIf(t -> t.id().equals(id));commit(next);
    }
    public synchronized void clearCompleted() throws IOException { commit(tasks.stream().filter(t -> !t.done()).toList()); }
    private void commit(List<PlannerTask> next) throws IOException {
        if (writable) {
            Path parent=file.toAbsolutePath().getParent();Files.createDirectories(parent);
            try (java.nio.channels.FileChannel channel=java.nio.channels.FileChannel.open(parent.resolve("planner-v1.lock"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
                 java.nio.channels.FileLock lock=channel.lock()) {
                byte[] current=Files.exists(file)?Files.readAllBytes(file):null;
                if(!Arrays.equals(originalBytes,current))throw new IOException("Planner changed in another window. Restart to load the latest file.");
                String value=json(next);if(value.getBytes(StandardCharsets.UTF_8).length>2_000_000)throw new IOException("Planner file exceeds the 2 MB limit. Export or remove older tasks.");atomicWrite(file,value);originalBytes=value.getBytes(StandardCharsets.UTF_8);
            }
        }
        tasks.clear();tasks.addAll(next);
    }
    public String warning() { return warning; }
    public Path path() { return file; }
    public synchronized void export(Path target) throws IOException {
        if (target.toAbsolutePath().normalize().equals(file.toAbsolutePath().normalize()) || (Files.exists(target) && Files.exists(file) && Files.isSameFile(target,file))) throw new IOException("Choose a different export path to protect the active data file.");
        atomicWrite(target, json(tasks));
    }
    private static String json(List<PlannerTask> values) {
        JsonObject root = new JsonObject();root.addProperty("version",1);JsonArray rows=new JsonArray();
        for (PlannerTask t : values) {
            JsonObject row=new JsonObject();row.addProperty("id",t.id());row.addProperty("title",t.title());row.addProperty("notes",t.notes());row.addProperty("due",t.due().toString());row.addProperty("category",t.category());row.addProperty("done",t.done());rows.add(row);
        }
        root.add("tasks",rows);return new GsonBuilder().setPrettyPrinting().create().toJson(root);
    }
    private static void atomicWrite(Path target, String value) throws IOException {
        Path parent=target.toAbsolutePath().getParent();Files.createDirectories(parent);
        Path temp=Files.createTempFile(parent,".campus-", ".tmp");
        try {
            Files.writeString(temp,value,StandardCharsets.UTF_8);
            try {Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch (AtomicMoveNotSupportedException e) {Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING);}
        } finally {Files.deleteIfExists(temp);}
    }
}
