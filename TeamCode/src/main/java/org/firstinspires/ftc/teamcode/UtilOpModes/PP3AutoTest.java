package org.firstinspires.ftc.teamcode.UtilOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.Command;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.api.Paths.*;


@Autonomous
public class PP3AutoTest extends OpMode {
    private Follower follower;

    //create poseFactory that uses degrees (radians big :( )
    private final PoseFactory poseFactory = PoseFactory.degrees();

    //pose
    private final Pose start = poseFactory.of(24, 24, 0);
    private final Pose corner1 = poseFactory.of(48, 24, 0);
    private final Pose corner2 = poseFactory.of(48, 48, 0);
    private final Pose corner3 = poseFactory.of(24, 48, 0);

    //path helper methods

    private Path first() {
        return line(start, corner1).facingPoint(start);
    }
    private Path second() {
        return line(corner1, corner2).facingPoint(corner1);
    }
    private Path third() {
        return line(corner2, corner3).facingPoint(corner2);
    }private Path fourth() {
        return line(corner3, start).facingPoint(corner3);
    }

//    private final Pose start = poseFactory.of(56, 8, 90);
//    private final Pose path1 = poseFactory.of(56, 36, 180);
//    private final Pose point2 = poseFactory.of(26, 36, 270);
//    private final Pose point3 = poseFactory.of(26, 8, 90);
//    private final Pose point4 = poseFactory.of(56, 8, 180);
//
//    public Path path1() {
//        return line(start, path1).linear(start,path1);
//    }
//
//    public Path path2() {
//        return line(path1, point2).linear(path1, point2);
//    }
//
//    public Path path3() {
//        return line(point2, point3).linear(point2, point3);
//    }
//
//    public Path path4() {
//        return line(point3, point4).linear(point3, point4);
//    }


    //routines
    private Command testRoutine() {
        return sequential(
                follow(follower, first()),
                // Add mechanism commands here.
                follow(follower, second()),
                follow(follower, third()),
                follow(follower, fourth())
        );
    }

    @Override
    public void init() {
        //Reset movements
        Scheduler.reset();

        //initializes follower
        follower = Constants.create(hardwareMap);

        //sets pose to "start"
        follower.setPose(start);
        follower.update();
    }

    @Override
    public void start() {
        schedule(testRoutine());
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
    }
}
