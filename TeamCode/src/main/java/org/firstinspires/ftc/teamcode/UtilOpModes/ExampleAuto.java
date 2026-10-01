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
    public class ExampleAuto extends OpMode {
        private Follower follower;
        private final PoseFactory poseFactory = PoseFactory.degrees();

        // Poses
        private final Pose q = poseFactory.of(24, 24, 0);
        private final Pose w = poseFactory.of(48, 24, 90);
        private final Pose e = poseFactory.of(48, 48, 0);
        private final Pose r = poseFactory.of(24, 48, 90);

        // Path methods
        private Path one() {
            return line(q, w).linear(q, w);
        }

        private Path two(){
            return line(w, e).linear(w, e);
        }

        private Path three(){
            return line(e, r).linear(e, r);
        }

        private Path four(){
            return line(r, q).linear(r, q);
        }

        private Command autoRoutine() {
            return sequential(
                    follow(follower, one()),
                    // Add mechanism commands here.
                    follow(follower, two()),
                    follow(follower, three()),
                    follow(follower, four())

            );
        }

        @Override
        public void init() {
            Scheduler.reset();

            follower = Constants.create(hardwareMap);
            follower.setPose(q);
            follower.update();
        }

        @Override
        public void start() {
            schedule(autoRoutine());
        }

        @Override
        public void loop() {
            follower.update();
            Scheduler.execute();
            // add your other methods needed in the loop here

//            telemetryData.addData("X", follower.pose().x());
//            telemetryData.addData("Y", follower.pose().y());
//            telemetryData.addData("Heading", Math.toDegrees(follower.pose().heading()));
            telemetry.addData("Follower Mode", follower.mode());
            telemetry.update();
        }
    }

