package com.ljuniversity.companion;
import com.ljuniversity.companion.model.PlannerTask;
import com.ljuniversity.companion.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PlannerTest {
 @TempDir Path dir;
 PlannerTask task(String title,String due){return PlannerTask.create(title,"Example notes",due,"Academic");}
 @Test void startsEmptyAndDoesNotCreateSampleRecords(){var s=new PlannerStore(dir.resolve("tasks.json"));assertTrue(s.all().isEmpty());assertFalse(Files.exists(s.path()));}
 @Test void savesExactTaskAndReloads() throws Exception{var s=new PlannerStore(dir.resolve("tasks.json"));var t=task("A \"quoted\" title ","2026-12-01");s.put(t);var r=new PlannerStore(s.path());assertEquals(t,r.all().getFirst());assertFalse(r.list("quoted","All tasks").isEmpty());}
 @Test void searchAndFilterCombine() throws Exception{var s=new PlannerStore(dir.resolve("tasks.json"));var t=task("Read notes","2026-12-01");s.put(t);s.put(task("Make prototype","2026-12-03").completed(true));assertEquals(1,s.list(" NOTES ","Open").size());assertTrue(s.list("prototype","Open").isEmpty());assertEquals(1,s.list("","Completed").size());}
 @Test void editUsesStableIdAndCompletion() throws Exception{var s=new PlannerStore(dir.resolve("tasks.json"));var t=task("Draft","2026-12-01");s.put(t);s.put(new PlannerTask(t.id(),"Final","",t.due(),t.category(),true));assertEquals(1,s.all().size());assertTrue(s.all().getFirst().done());}
 @Test void rejectsBadDatesAndEmptyTitles(){assertThrows(RuntimeException.class,()->task("Title","2026-02-30"));assertThrows(IllegalArgumentException.class,()->task("   ","2026-12-01"));assertThrows(IllegalArgumentException.class,()->task("a".repeat(121),"2026-12-01"));}
 @Test void dueDateAndOverdueAreExplicit(){var t=task("Plan","2026-12-01");assertFalse(t.overdue(LocalDate.of(2026,12,1)));assertTrue(t.overdue(LocalDate.of(2026,12,2)));assertFalse(t.completed(true).overdue(LocalDate.of(2026,12,2)));}
 @Test void resetOnlyRemovesCompleted() throws Exception{var s=new PlannerStore(dir.resolve("tasks.json"));s.put(task("Keep","2026-12-01"));s.put(task("Done","2026-12-01").completed(true));s.clearCompleted();assertEquals("Keep",new PlannerStore(s.path()).all().getFirst().title());}
 @Test void corruptFileIsUntouchedAndSessionCanExport() throws Exception{Path file=dir.resolve("tasks.json");Files.writeString(file,"not json");var s=new PlannerStore(file);assertNotNull(s.warning());s.put(task("Session only","2026-12-01"));assertEquals("not json",Files.readString(file));Path export=dir.resolve("export.json");s.export(export);assertEquals(1,new PlannerStore(export).all().size());}
 @Test void activeFileCannotBeExportOverwritten() throws Exception{var s=new PlannerStore(dir.resolve("tasks.json"));s.put(task("One","2026-12-01"));assertThrows(java.io.IOException.class,()->s.export(s.path()));}
 @Test void staleWindowCannotOverwriteNewerWork() throws Exception{Path file=dir.resolve("tasks.json");var first=new PlannerStore(file);var stale=new PlannerStore(file);first.put(task("First","2026-12-01"));assertThrows(java.io.IOException.class,()->stale.put(task("Second","2026-12-01")));assertTrue(stale.all().isEmpty());assertEquals("First",new PlannerStore(file).all().getFirst().title());}
 @Test void exportIsRealReloadableJson() throws Exception{var s=new PlannerStore(dir.resolve("tasks.json"));s.put(task("Study","2026-12-01"));Path out=dir.resolve("out.json");s.export(out);assertEquals(s.all(),new PlannerStore(out).all());}
 @Test void sqlLegacyPasswordHashesAreSaltedAndRejectPlaintext(){String a=Passwords.hash("test-only-password"),b=Passwords.hash("test-only-password");assertNotEquals(a,b);assertTrue(Passwords.verify("test-only-password",a));assertFalse(Passwords.verify("wrong",a));assertFalse(Passwords.verify("test-only-password","test-only-password"));}
 @Test void invalidHashAndHugeWorkFactorsAreRejected(){assertFalse(Passwords.verify("test",null));assertFalse(Passwords.verify("test","pbkdf2-sha256$999999999$bad$bad"));assertFalse(Passwords.verify("test","pbkdf2-sha256$abc$bad$bad"));}
 @Test void optionalAiDoesNotRequestWithoutKey(){assertNull(System.getenv("GEMINI_API_KEY"));assertTrue(new GeminiClient().sendMessage("test").contains("not connected"));}
 @Test void rankingIsDescending(){BST tree=new BST();tree.insert("One","1",10);tree.insert("Two","2",50);tree.insert("Three","3",25);assertEquals(50,tree.getSortedData().getFirst().totalPoints);assertEquals(10,tree.getSortedData().getLast().totalPoints);}
}
